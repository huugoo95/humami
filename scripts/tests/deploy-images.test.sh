#!/usr/bin/env bash
# Runs scripts/deploy-images.sh against a simulated host (tests/fake-docker) and checks that
# nginx never routes to a stopped container, that unchanged services are left alone, and that
# failed releases leave the previous one serving. Usage: scripts/tests/deploy-images.test.sh
set -euo pipefail

TESTS_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_DIR="$(cd "$TESTS_DIR/../.." && pwd)"
WORK_DIR="$(mktemp -d)"
trap 'rm -rf "$WORK_DIR"' EXIT

A=aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
B=bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb
C=cccccccccccccccccccccccccccccccccccccccc
OWNER=huugoo95
failures=0
current=""

# A fresh host: app root, fake docker on PATH, a smoke check that hits whatever nginx routes to.
new_host() {
  current="$1"
  export SIM_DIR="$WORK_DIR/$1/sim" SIM_ROOT="$WORK_DIR/$1/app"
  mkdir -p "$SIM_DIR/bin" "$SIM_ROOT/scripts" "$SIM_ROOT/nginx"
  cp "$TESTS_DIR/fake-docker" "$SIM_DIR/bin/docker"
  cp "$REPO_DIR/scripts/deploy-images.sh" "$SIM_ROOT/scripts/"
  cp "$REPO_DIR/docker-compose.images.yml" "$SIM_ROOT/"
  cp "$REPO_DIR/nginx.conf" "$SIM_ROOT/nginx/site.conf"
  echo "SECRET=1" > "$SIM_ROOT/.env"
  echo "MemAvailable:    1048576 kB" > "$SIM_DIR/meminfo"
  : > "$SIM_DIR/events.log"
  cat > "$SIM_ROOT/scripts/smoke-prod.sh" <<'SMOKE'
#!/usr/bin/env bash
for host in $(cat "$SIM_DIR/nginx-live" 2>/dev/null); do
  dir="$SIM_DIR/containers/humami-$host"
  tag="$(sed 's/.*://' "$dir/image" 2>/dev/null)"
  if [[ "$(cat "$dir/status" 2>/dev/null)" != running || " ${SIM_SMOKE_FAIL_TAGS:-} " == *" $tag "* ]]; then
    echo "SMOKE-FAIL $host" >> "$SIM_DIR/events.log"; exit 1
  fi
done
echo "SMOKE-OK" >> "$SIM_DIR/events.log"
SMOKE
  chmod +x "$SIM_ROOT/scripts/smoke-prod.sh"
  unset SIM_PROBES_$A SIM_PROBES_$B SIM_PROBES_$C SIM_SMOKE_FAIL_TAGS SIM_NGINX_T_FAIL SIM_DEF_NGINX 2>/dev/null || true
}

fake() { (cd "$SIM_ROOT" && PATH="$SIM_DIR/bin:$PATH" GHCR_OWNER=$OWNER docker "$@"); }

seed() { # name service-for-hash tag; a running, ready container as a previous deploy left it
  local name="$1" service="$2" tag="$3" image hash dir="$SIM_DIR/containers/$1"
  case "$name" in
    *backend*) image="ghcr.io/$OWNER/humami-backend:$tag" ;;
    *frontend*) image="ghcr.io/$OWNER/humami-frontend:$tag" ;;
    *) image=nginx:alpine ;;
  esac
  hash="$(BACKEND_BLUE_TAG=$tag BACKEND_GREEN_TAG=$tag FRONTEND_BLUE_TAG=$tag FRONTEND_GREEN_TAG=$tag fake compose config --hash "$service" | awk '{print $2}')"
  mkdir -p "$dir"
  echo "$image" > "$dir/image"; echo "$hash" > "$dir/hash"; echo running > "$dir/status"
  echo 0 > "$dir/restarts"; echo 0 > "$dir/probes"; echo 0 > "$dir/needed"
}

legacy_host() { # tag: the single-container layout before slots
  seed humami-backend backend-blue "$1"
  seed humami-frontend frontend-blue "$1"
  seed humami-nginx nginx x
  printf 'server {\n  location /api/ {\n    proxy_pass http://backend:8080/api/;\n  }\n  location / {\n    proxy_pass http://frontend:3000/;\n  }\n}\n' > "$SIM_ROOT/nginx.conf"
  printf 'backend\nfrontend\n' > "$SIM_DIR/nginx-live"
}

slot_host() { # backend_slot backend_tag frontend_slot frontend_tag
  seed "humami-backend-$1" "backend-$1" "$2"
  seed "humami-frontend-$3" "frontend-$3" "$4"
  seed humami-nginx nginx x
  printf 'BACKEND_SLOT=%s\nFRONTEND_SLOT=%s\n' "$1" "$3" > "$SIM_ROOT/release.state"
  printf 'upstream humami_backend {\n  server backend-%s:8080;\n}\nupstream humami_frontend {\n  server frontend-%s:3000;\n}\n' "$1" "$3" > "$SIM_ROOT/nginx.conf"
  printf 'backend-%s\nfrontend-%s\n' "$1" "$3" > "$SIM_DIR/nginx-live"
}

deploy() { # backend_tag frontend_tag; sets $status and $output
  set +e
  output="$(cd "$SIM_ROOT" && PATH="$SIM_DIR/bin:$PATH" GHCR_OWNER=$OWNER BACKEND_TAG="$1" FRONTEND_TAG="$2" \
    MEMINFO_FILE="$SIM_DIR/meminfo" MIN_FREE_MB=1 READY_TIMEOUT_SECONDS=1 READY_INTERVAL_SECONDS=0 \
    DRAIN_SECONDS=0 ./scripts/deploy-images.sh 2>&1)"
  status=$?
  set -e
}

fail() {
  echo "  FAIL [$current] $*"
  failures=$((failures + 1))
}

expect_status() { [[ "$status" == "$1" ]] || { fail "exit $status, expected $1"; echo "$output" | sed 's/^/    | /'; }; }
expect_live() { local live; live="$(tr '\n' ' ' < "$SIM_DIR/nginx-live")"; [[ "$live" == "$1 " ]] || fail "nginx routes to '$live', expected '$1'"; }
expect_running() { local name; for name in "$@"; do [[ "$(cat "$SIM_DIR/containers/$name/status" 2>/dev/null)" == running ]] || fail "$name not running"; done; }
expect_absent() { local name; for name in "$@"; do [[ ! -d "$SIM_DIR/containers/$name" ]] || fail "$name still exists"; done; }
expect_image() { [[ "$(cat "$SIM_DIR/containers/$1/image")" == *":$2" ]] || fail "$1 runs $(cat "$SIM_DIR/containers/$1/image")"; }
expect_event() { grep -q -- "$1" "$SIM_DIR/events.log" || fail "no event '$1'"; }
expect_no_event() { ! grep -q -- "$1" "$SIM_DIR/events.log" || fail "unexpected event: $(grep -- "$1" "$SIM_DIR/events.log" | head -n 1)"; }
expect_output() { [[ "$output" == *"$1"* ]] || fail "output lacks '$1'"; }
expect_state() { [[ "$(tr '\n' ' ' < "$SIM_ROOT/release.state")" == "$1 " ]] || fail "state is '$(tr '\n' ' ' < "$SIM_ROOT/release.state")'"; }
nginx_inode() { ls -i "$SIM_ROOT/nginx.conf" | awk '{print $1}'; }

echo "migration from single containers starts both slots before switching"
new_host migration; legacy_host "$A"; inode="$(nginx_inode)"
deploy "$B" "$B"
expect_status 0
expect_live "backend-blue frontend-blue"
expect_running humami-backend-blue humami-frontend-blue humami-nginx
expect_image humami-backend-blue "$B"
expect_absent humami-backend humami-frontend
expect_no_event DOWNTIME
expect_no_event NGINX-RECREATED
expect_state "BACKEND_SLOT=blue FRONTEND_SLOT=blue"
[[ "$(nginx_inode)" == "$inode" ]] || fail "nginx.conf was replaced instead of rewritten in place"
grep -q "server backend-blue:8080;" "$SIM_ROOT/nginx.conf" || fail "nginx.conf lacks the backend upstream"

echo "migration keeps the running tag but still moves it into a slot"
new_host migration-same-tag; legacy_host "$A"
deploy "$A" "$A"
expect_status 0
expect_live "backend-blue frontend-blue"
expect_absent humami-backend humami-frontend
expect_no_event DOWNTIME

echo "only the changed service is replaced"
new_host backend-only; slot_host blue "$A" blue "$A"
deploy "$B" "$A"
expect_status 0
expect_live "backend-green frontend-blue"
expect_image humami-backend-green "$B"
expect_absent humami-backend-blue
expect_running humami-frontend-blue
expect_no_event "CREATE humami-frontend"
expect_no_event "PULL frontend"
expect_no_event DOWNTIME
expect_state "BACKEND_SLOT=green FRONTEND_SLOT=blue"

echo "next release goes back to the other slot"
deploy "$C" "$C"
expect_status 0
expect_live "backend-blue frontend-green"
expect_absent humami-backend-green humami-frontend-blue
expect_no_event DOWNTIME

echo "nothing changed: no container is touched"
new_host noop; slot_host green "$A" blue "$A"
deploy "$A" "$A"
expect_status 0
expect_no_event CREATE
expect_no_event STOP
expect_no_event "RM "
expect_event SMOKE-OK
expect_live "backend-green frontend-blue"

echo "a changed .env redeploys both services with the same tags"
new_host env-change; slot_host blue "$A" blue "$A"
echo "SECRET=2" > "$SIM_ROOT/.env"
deploy "$A" "$A"
expect_status 0
expect_live "backend-green frontend-green"
expect_no_event DOWNTIME

echo "a release that never gets ready is never switched to"
new_host not-ready; slot_host blue "$A" blue "$A"
export SIM_PROBES_$B=never
deploy "$B" "$A"
expect_status 1
expect_live "backend-blue frontend-blue"
expect_absent humami-backend-green
expect_running humami-backend-blue
expect_no_event DOWNTIME
expect_no_event NGINX-LIVE
expect_state "BACKEND_SLOT=blue FRONTEND_SLOT=blue"
expect_output "production still runs the previous release"

echo "a crashing release fails fast and is removed"
new_host crash; slot_host blue "$A" blue "$A"
export SIM_PROBES_$B=crash
deploy "$B" "$B"
expect_status 1
expect_live "backend-blue frontend-blue"
expect_absent humami-backend-green humami-frontend-green
expect_no_event DOWNTIME

echo "failed smoke checks switch nginx back to the old containers"
new_host smoke-fail; slot_host blue "$A" blue "$A"; inode="$(nginx_inode)"
export SIM_SMOKE_FAIL_TAGS="$B"
deploy "$B" "$B"
expect_status 1
expect_live "backend-blue frontend-blue"
expect_running humami-backend-blue humami-frontend-blue
expect_absent humami-backend-green humami-frontend-green
expect_no_event DOWNTIME
expect_output "Rolled back to the previous release"
expect_state "BACKEND_SLOT=blue FRONTEND_SLOT=blue"
[[ "$(nginx_inode)" == "$inode" ]] || fail "nginx.conf inode changed"

echo "failed smoke checks during migration restore the legacy nginx config"
new_host smoke-fail-migration; legacy_host "$A"
export SIM_SMOKE_FAIL_TAGS="$B"
deploy "$B" "$B"
expect_status 1
expect_live "backend frontend"
expect_running humami-backend humami-frontend
expect_absent humami-backend-blue humami-frontend-blue
expect_no_event DOWNTIME
[[ ! -f "$SIM_ROOT/release.state" ]] || fail "release.state written for a failed release"

echo "nginx rejecting the config leaves the old release serving"
new_host nginx-reject; slot_host blue "$A" blue "$A"
echo "BROKEN" >> "$SIM_ROOT/nginx/site.conf"
deploy "$B" "$A"
expect_status 1
expect_live "backend-blue frontend-blue"
expect_absent humami-backend-green
expect_no_event DOWNTIME
grep -q BROKEN "$SIM_ROOT/nginx.conf" && fail "broken nginx.conf left in place"

echo "a changed nginx definition recreates nginx after the slots are ready"
new_host nginx-drift; slot_host blue "$A" blue "$A"
export SIM_DEF_NGINX=v2
deploy "$B" "$A"
expect_status 0
expect_event NGINX-RECREATED
expect_live "backend-green frontend-blue"

echo "low memory stops the old backend first and restarts it if the release fails"
new_host low-memory; slot_host blue "$A" blue "$A"
echo "MemAvailable:     204800 kB" > "$SIM_DIR/meminfo"
export SIM_PROBES_$B=never
deploy "$B" "$A"
expect_status 1
expect_output "Only 200 MB available"
expect_event "STOP humami-backend-blue"
expect_event "START humami-backend-blue"
expect_running humami-backend-blue
expect_absent humami-backend-green
expect_live "backend-blue frontend-blue"

echo "low memory still completes a good release"
new_host low-memory-ok; slot_host blue "$A" blue "$A"
echo "MemAvailable:     204800 kB" > "$SIM_DIR/meminfo"
deploy "$B" "$A"
expect_status 0
expect_live "backend-green frontend-blue"
expect_absent humami-backend-blue

echo "leftovers in the idle slot are replaced"
new_host stale; slot_host blue "$A" blue "$A"
seed humami-backend-green backend-green "$C"; echo exited > "$SIM_DIR/containers/humami-backend-green/status"
deploy "$B" "$A"
expect_status 0
expect_image humami-backend-green "$B"
expect_event "RM humami-backend-green"

echo "tags must be full SHAs"
new_host bad-tag; slot_host blue "$A" blue "$A"
deploy latest "$A"
expect_status 1
expect_no_event CREATE
expect_output "must be full commit SHAs"

echo "--status reports the running tags, legacy or slots"
new_host status-legacy; legacy_host "$A"
out="$(cd "$SIM_ROOT" && PATH="$SIM_DIR/bin:$PATH" ./scripts/deploy-images.sh --status)"
[[ "$out" == "backend=$A"$'\n'"frontend=$A" ]] || fail "status: $out"
new_host status-slots; slot_host green "$B" blue "$A"
out="$(PATH="$SIM_DIR/bin:$PATH" HUMAMI_ROOT="$SIM_ROOT" bash -s -- --status < "$REPO_DIR/scripts/deploy-images.sh")"
[[ "$out" == "backend=$B"$'\n'"frontend=$A" ]] || fail "status over stdin: $out"
new_host status-empty
out="$(cd "$SIM_ROOT" && PATH="$SIM_DIR/bin:$PATH" ./scripts/deploy-images.sh --status)"
[[ "$out" == "backend="$'\n'"frontend=" ]] || fail "empty status: $out"

if (( failures > 0 )); then
  echo "${failures} check(s) failed"
  exit 1
fi
echo "All deploy-images.sh scenarios passed"
