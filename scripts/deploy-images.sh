#!/usr/bin/env bash
# Zero-downtime production deploy. Backend and frontend each run in a blue or green slot.
# A service whose image or Compose definition changed gets its new release started in the
# idle slot next to the running one; once it answers locally, nginx is pointed at it with a
# reload (no restart), smoke checks run, and only then is the old slot removed. If anything
# fails before that point, the old containers are still serving and nginx is switched back.
#
# Usage: BACKEND_TAG=<sha> FRONTEND_TAG=<sha> GHCR_OWNER=<owner> scripts/deploy-images.sh
#        (IMAGE_TAG=<sha> sets both tags.)
#        scripts/deploy-images.sh --status   prints the running tags, read-only.
set -euo pipefail

ROOT_DIR="${HUMAMI_ROOT:-$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)}"
cd "$ROOT_DIR"

STATE_FILE=release.state
SITE_CONF=nginx/site.conf
NGINX_CONF=nginx.conf
NGINX_CONTAINER=humami-nginx

container_status() {
  docker inspect --type container --format '{{.State.Status}}' "$1" 2>/dev/null || true
}

container_tag() {
  docker inspect --type container --format '{{.Config.Image}}' "$1" 2>/dev/null | sed 's/.*://' || true
}

# The slot (blue|green) recorded for a service, if its container is running; "legacy" for the
# single-container layout used before slots; empty when nothing serves the service.
active_slot() {
  local service="$1" key slot=""
  key="$(printf '%s_SLOT' "$service" | tr '[:lower:]' '[:upper:]')"
  if [[ -f "$STATE_FILE" ]]; then
    slot="$(awk -F= -v key="$key" '$1 == key && ($2 == "blue" || $2 == "green") {print $2}' "$STATE_FILE")"
  fi
  if [[ -n "$slot" && "$(container_status "humami-${service}-${slot}")" == running ]]; then
    echo "$slot"
  elif [[ "$(container_status "humami-${service}")" == running ]]; then
    echo legacy
  fi
}

container_name() { # service slot
  if [[ "$2" == legacy ]]; then echo "humami-$1"; else echo "humami-$1-$2"; fi
}

if [[ "${1:-}" == --status ]]; then
  for service in backend frontend; do
    slot="$(active_slot "$service")"
    tag=""
    [[ -n "$slot" ]] && tag="$(container_tag "$(container_name "$service" "$slot")")"
    echo "${service}=${tag}"
  done
  exit 0
fi

: "${GHCR_OWNER:=huugoo95}"
BACKEND_TAG="${BACKEND_TAG:-${IMAGE_TAG:-}}"
FRONTEND_TAG="${FRONTEND_TAG:-${IMAGE_TAG:-}}"
: "${MIN_FREE_MB:=3072}"
# Two JVMs run side by side while the backend switches. Below this much available memory the
# old backend is stopped first instead (a short outage, as before slots existed).
: "${MIN_PARALLEL_MEM_MB:=600}"
: "${MEMINFO_FILE:=/proc/meminfo}"
: "${READY_TIMEOUT_SECONDS:=600}"
: "${READY_INTERVAL_SECONDS:=5}"
: "${SMOKE_TIMEOUT_SECONDS:=90}"
: "${DRAIN_SECONDS:=10}"
: "${SMOKE_URL:=https://humami.es}"

# Production only runs exact master SHAs.
for tag in "$BACKEND_TAG" "$FRONTEND_TAG"; do
  if [[ ! "$tag" =~ ^[0-9a-f]{40}$ ]]; then
    echo "[deploy] BACKEND_TAG and FRONTEND_TAG (or IMAGE_TAG) must be full commit SHAs; got '${tag}'."
    exit 1
  fi
done
if [[ ! -f "$SITE_CONF" ]]; then
  echo "[deploy] Missing ${SITE_CONF}; the workflow copies the repository's nginx.conf there."
  exit 1
fi

if [[ -n "${GHCR_TOKEN:-}" ]]; then
  echo "$GHCR_TOKEN" | docker login ghcr.io -u "${GHCR_USERNAME:-$GHCR_OWNER}" --password-stdin
fi

export GHCR_OWNER BACKEND_BLUE_TAG BACKEND_GREEN_TAG FRONTEND_BLUE_TAG FRONTEND_GREEN_TAG

compose() {
  docker compose -f docker-compose.images.yml --profile prod "$@"
}

compose_hash() {
  compose config --hash "$1" | awk '{print $2}'
}

container_hash() {
  docker inspect --type container --format '{{index .Config.Labels "com.docker.compose.config-hash"}}' "$1" 2>/dev/null || true
}

report_memory() {
  echo "[deploy] Memory ($1):"
  free -m 2>/dev/null || true
  docker stats --no-stream --format '  {{.Name}}: {{.MemUsage}} CPU {{.CPUPerc}}' 2>/dev/null || true
}

# Image layers live under the Docker root or, with the containerd image store, under /var/lib/containerd.
storage_paths=("$(docker info --format '{{.DockerRootDir}}')")
[[ -d /var/lib/containerd ]] && storage_paths+=(/var/lib/containerd)

report_disk() {
  echo "[deploy] Disk usage ($1):"
  df -h / "${storage_paths[@]}" || true
  docker system df || true
}

# --- Plan: which services need a new container ------------------------------------------------

backend_active="$(active_slot backend)"
frontend_active="$(active_slot frontend)"
backend_target=""
frontend_target=""
deploying=()

plan_service() { # service desired_tag active_slot -> prints the target slot, or nothing to keep the active one
  local service="$1" desired="$2" active="$3" upper
  upper="$(printf '%s' "$service" | tr '[:lower:]' '[:upper:]')"
  export "${upper}_BLUE_TAG=$desired" "${upper}_GREEN_TAG=$desired"
  if [[ "$active" == blue || "$active" == green ]]; then
    local want have
    want="$(compose_hash "${service}-${active}")"
    have="$(container_hash "humami-${service}-${active}")"
    if [[ -n "$want" && "$want" == "$have" ]]; then
      return 0
    fi
  fi
  if [[ "$active" == blue ]]; then echo green; else echo blue; fi
}

backend_target="$(plan_service backend "$BACKEND_TAG" "$backend_active")"
frontend_target="$(plan_service frontend "$FRONTEND_TAG" "$frontend_active")"
# plan_service ran in subshells; export the slot tags here.
export BACKEND_BLUE_TAG="$BACKEND_TAG" BACKEND_GREEN_TAG="$BACKEND_TAG"
export FRONTEND_BLUE_TAG="$FRONTEND_TAG" FRONTEND_GREEN_TAG="$FRONTEND_TAG"
[[ -n "$backend_target" ]] && deploying+=(backend)
[[ -n "$frontend_target" ]] && deploying+=(frontend)

describe() { # service active target desired
  if [[ -n "$3" ]]; then
    echo "[deploy] ${1}: ${2:-nothing running} -> ${3} slot with ${4}"
  else
    echo "[deploy] ${1}: unchanged in ${2} slot ($(container_tag "humami-${1}-${2}"))"
  fi
}
describe backend "$backend_active" "$backend_target" "$BACKEND_TAG"
describe frontend "$frontend_active" "$frontend_target" "$FRONTEND_TAG"

# --- Housekeeping -----------------------------------------------------------------------------

# The dev environment was retired (ops/decisions.md, 2026-10-19): remove its containers and network if present.
# Volumes, including the old dev MongoDB data, are kept for the owner to delete.
for container in humami-dev-backend humami-dev-frontend humami-dev-dev-mongo-1; do
  docker rm -f "$container" >/dev/null 2>&1 && echo "[deploy] Removed retired dev container ${container}" || true
done
docker network rm humami-dev-ingress >/dev/null 2>&1 && echo "[deploy] Removed retired dev network" || true

# Single containers from before slots existed, once a slot serves the service.
for service in backend frontend; do
  active_var="${service}_active"
  if [[ "${!active_var}" == blue || "${!active_var}" == green ]] && [[ -n "$(container_status "humami-${service}")" ]]; then
    docker rm -f "humami-${service}" >/dev/null && echo "[deploy] Removed pre-slot container humami-${service}"
  fi
done

# Leftovers in a target slot (for example from an interrupted deploy) never serve traffic.
for service in "${deploying[@]+"${deploying[@]}"}"; do
  target_var="${service}_target"
  stale="humami-${service}-${!target_var}"
  if [[ -n "$(container_status "$stale")" ]]; then
    docker rm -f "$stale" >/dev/null && echo "[deploy] Removed stale ${stale}"
  fi
done

# Reclaim images no container uses (running containers keep theirs) and the build cache. Volumes are never pruned.
report_disk "before cleanup"
docker image prune -af
docker builder prune -af
report_disk "after cleanup"

free_mb="$(df -Pm "${storage_paths[@]}" | awk 'NR > 1 {print $4}' | sort -n | head -n 1)"
if [[ ! "$free_mb" =~ ^[0-9]+$ ]]; then
  echo "[deploy] Could not read free space under ${storage_paths[*]}. Aborting before pull."
  exit 1
fi
if (( free_mb < MIN_FREE_MB )); then
  echo "[deploy] Only ${free_mb} MB free under ${storage_paths[*]}; ${MIN_FREE_MB} MB required. Aborting before pull."
  exit 1
fi
echo "[deploy] ${free_mb} MB free under ${storage_paths[*]}"

# --- Start the new slots next to the old ones ------------------------------------------------

target_services=()
for service in "${deploying[@]+"${deploying[@]}"}"; do
  target_var="${service}_target"
  target_services+=("${service}-${!target_var}")
done

# Old containers stopped (not removed) to make room; restarted if the release fails.
stopped_old=()

restore_stopped() {
  local name
  for name in "${stopped_old[@]+"${stopped_old[@]}"}"; do
    docker start "$name" >/dev/null && echo "[deploy] Restarted ${name}" || echo "[deploy] Could not restart ${name}"
  done
}

remove_targets() {
  local name
  for name in "${target_services[@]+"${target_services[@]}"}"; do
    docker rm -f "humami-${name}" >/dev/null 2>&1 || true
  done
}

if (( ${#target_services[@]} > 0 )); then
  compose pull "${target_services[@]}"
  report_memory "before start"

  if [[ -n "$backend_target" && ( "$backend_active" == blue || "$backend_active" == green || "$backend_active" == legacy ) ]]; then
    available_mb="$(awk '/^MemAvailable:/ {print int($2 / 1024)}' "$MEMINFO_FILE" 2>/dev/null || true)"
    if [[ "$available_mb" =~ ^[0-9]+$ ]] && (( available_mb < MIN_PARALLEL_MEM_MB )); then
      old_backend="$(container_name backend "$backend_active")"
      echo "[deploy] Only ${available_mb} MB available (< ${MIN_PARALLEL_MEM_MB} MB): stopping ${old_backend} before starting the new backend. /api is down until it is ready."
      docker stop -t 30 "$old_backend" >/dev/null
      stopped_old+=("$old_backend")
    else
      echo "[deploy] ${available_mb:-unknown} MB available; starting the new backend next to the running one."
    fi
  fi

  if ! compose up -d --no-deps --force-recreate "${target_services[@]}"; then
    echo "[deploy] Could not start ${target_services[*]}; production unchanged."
    remove_targets
    restore_stopped
    exit 1
  fi
fi

probe() { # container port path
  docker exec "$1" wget -q -O /dev/null -T 10 "http://127.0.0.1:${2}${3}" >/dev/null 2>&1
}

wait_ready() {
  local deadline=$(( $(date +%s) + READY_TIMEOUT_SECONDS )) name status restarts
  local pending=("${target_services[@]}") next=()
  while (( ${#pending[@]} > 0 )); do
    next=()
    for name in "${pending[@]}"; do
      status="$(container_status "humami-${name}")"
      restarts="$(docker inspect --type container --format '{{.RestartCount}}' "humami-${name}" 2>/dev/null || echo 0)"
      if [[ "$status" == exited || "$status" == dead || -z "$status" || "$restarts" -ge 3 ]]; then
        echo "[deploy] humami-${name} is ${status:-missing} (restarts: ${restarts}); last log lines:"
        docker logs --tail 40 "humami-${name}" 2>&1 || true
        return 1
      fi
      case "$name" in
        backend-*) probe "humami-${name}" 8080 "/api/meals?query=&page=1&limit=5" ;;
        frontend-*) probe "humami-${name}" 3000 "/" ;;
      esac && { echo "[deploy] humami-${name} is ready"; continue; }
      next+=("$name")
    done
    (( ${#next[@]} == 0 )) && return 0
    pending=("${next[@]}")
    if (( $(date +%s) >= deadline )); then
      echo "[deploy] Not ready within ${READY_TIMEOUT_SECONDS}s: ${pending[*]}"
      for name in "${pending[@]}"; do docker logs --tail 40 "humami-${name}" 2>&1 || true; done
      return 1
    fi
    sleep "$READY_INTERVAL_SECONDS"
  done
}

if (( ${#target_services[@]} > 0 )); then
  start_seconds=$(date +%s)
  if ! wait_ready; then
    echo "[deploy] Release did not become ready; nginx was not switched and production still runs the previous release."
    remove_targets
    restore_stopped
    exit 1
  fi
  echo "[deploy] New containers ready after $(( $(date +%s) - start_seconds ))s"
  report_memory "both releases running"
fi

# --- Switch nginx -----------------------------------------------------------------------------

upstream_host() { # service slot
  if [[ "$2" == legacy ]]; then echo "$1"; else echo "$1-$2"; fi
}

render_nginx() { # backend_host frontend_host
  {
    echo "# Rendered by scripts/deploy-images.sh from nginx/site.conf; do not edit on the host."
    printf 'upstream humami_backend {\n  server %s:8080;\n}\n\n' "$1"
    printf 'upstream humami_frontend {\n  server %s:3000;\n}\n\n' "$2"
    cat "$SITE_CONF"
  }
}

# Written in place: nginx bind-mounts this single file, so replacing it would leave nginx on the old inode.
write_nginx_conf() {
  cat > "$NGINX_CONF"
}

nginx_running() {
  [[ "$(container_status "$NGINX_CONTAINER")" == running ]]
}

previous_conf=""
[[ -f "$NGINX_CONF" ]] && previous_conf="$(cat "$NGINX_CONF")"

new_backend_slot="${backend_target:-$backend_active}"
new_frontend_slot="${frontend_target:-$frontend_active}"
render_nginx "$(upstream_host backend "$new_backend_slot")" "$(upstream_host frontend "$new_frontend_slot")" | write_nginx_conf

# Reload keeps serving: the master re-reads the config and old workers finish their requests.
# nginx is only (re)created when it is not running or its own Compose definition changed.
activate_nginx() {
  if nginx_running && [[ "$(compose_hash nginx)" == "$(container_hash "$NGINX_CONTAINER")" ]]; then
    docker exec "$NGINX_CONTAINER" nginx -t && docker exec "$NGINX_CONTAINER" nginx -s reload
  else
    echo "[deploy] (Re)creating nginx"
    compose up -d --no-deps --force-recreate nginx
  fi
}

switch_back() {
  echo "[deploy] Switching nginx back to the previous release."
  restore_stopped
  if [[ -n "$previous_conf" ]]; then
    printf '%s\n' "$previous_conf" | write_nginx_conf
    if nginx_running; then
      docker exec "$NGINX_CONTAINER" nginx -s reload || compose restart nginx || true
    fi
  fi
  remove_targets
}

if ! activate_nginx; then
  echo "[deploy] nginx rejected the new configuration."
  switch_back
  echo "[deploy] Rolled back: production still runs the previous release; ${BACKEND_TAG}/${FRONTEND_TAG} is NOT deployed."
  exit 1
fi

if ! WAIT_TIMEOUT_SECONDS="$SMOKE_TIMEOUT_SECONDS" ./scripts/smoke-prod.sh "$SMOKE_URL"; then
  echo "[deploy] Smoke checks failed after the switch."
  switch_back
  # A backend stopped for lack of memory needs its full start time again.
  rollback_timeout="$SMOKE_TIMEOUT_SECONDS"
  (( ${#stopped_old[@]} > 0 )) && rollback_timeout="$READY_TIMEOUT_SECONDS"
  if WAIT_TIMEOUT_SECONDS="$rollback_timeout" ./scripts/smoke-prod.sh "$SMOKE_URL"; then
    echo "[deploy] Rolled back to the previous release; ${BACKEND_TAG}/${FRONTEND_TAG} is NOT deployed."
  else
    echo "[deploy] Rollback did not pass smoke checks either; manual recovery needed."
  fi
  exit 1
fi

# --- Commit: record the active slots, then retire the old containers --------------------------

printf 'BACKEND_SLOT=%s\nFRONTEND_SLOT=%s\n' "$new_backend_slot" "$new_frontend_slot" > "$STATE_FILE.next"
mv "$STATE_FILE.next" "$STATE_FILE"

old_containers=()
[[ -n "$backend_target" && -n "$backend_active" ]] && old_containers+=("$(container_name backend "$backend_active")")
[[ -n "$frontend_target" && -n "$frontend_active" ]] && old_containers+=("$(container_name frontend "$frontend_active")")
if (( ${#old_containers[@]} > 0 )); then
  # Old nginx workers may still be finishing requests to the previous containers.
  sleep "$DRAIN_SECONDS"
  for name in "${old_containers[@]}"; do
    if docker stop -t 30 "$name" >/dev/null && docker rm "$name" >/dev/null; then
      echo "[deploy] Removed ${name}"
    else
      echo "[deploy] Warning: could not remove ${name}; the next deploy removes it."
    fi
  done
fi

echo "[deploy] Running backend ${BACKEND_TAG} (${new_backend_slot}) and frontend ${FRONTEND_TAG} (${new_frontend_slot})"
