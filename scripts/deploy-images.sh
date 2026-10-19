#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

: "${GHCR_OWNER:=huugoo95}"
: "${IMAGE_TAG:=latest}"

if [[ -n "${GHCR_TOKEN:-}" ]]; then
  if [[ -z "${GHCR_USERNAME:-}" ]]; then
    GHCR_USERNAME="$GHCR_OWNER"
  fi
  echo "$GHCR_TOKEN" | docker login ghcr.io -u "$GHCR_USERNAME" --password-stdin
fi

export GHCR_OWNER IMAGE_TAG

: "${MIN_FREE_MB:=3072}"

# Image layers live under the Docker root or, with the containerd image store, under /var/lib/containerd.
storage_paths=("$(docker info --format '{{.DockerRootDir}}')")
[[ -d /var/lib/containerd ]] && storage_paths+=(/var/lib/containerd)

report_disk() {
  echo "[deploy] Disk usage ($1):"
  df -h / "${storage_paths[@]}" || true
  docker system df || true
}

compose() {
  docker compose -f docker-compose.images.yml --profile prod "$@"
}

# The image tag currently serving production, used to roll back if the new release fails smoke checks.
previous_tag="$(docker inspect --type container --format '{{.Config.Image}}' humami-backend 2>/dev/null | sed 's/.*://')" || previous_tag=""
# Only exact master SHAs are valid rollback targets.
[[ "$previous_tag" =~ ^[0-9a-f]{40}$ ]] || previous_tag=""
echo "[deploy] Previous release: ${previous_tag:-none}"

# The dev environment was retired (ops/decisions.md, 2026-10-19): remove its containers and network if present.
# Volumes, including the old dev MongoDB data, are kept for the owner to delete.
for container in humami-dev-backend humami-dev-frontend humami-dev-dev-mongo-1; do
  docker rm -f "$container" >/dev/null 2>&1 && echo "[deploy] Removed retired dev container ${container}" || true
done
docker network rm humami-dev-ingress >/dev/null 2>&1 && echo "[deploy] Removed retired dev network" || true

# Every release pulls new SHA-tagged images; reclaim images no container uses (the running
# release keeps its own, so it stays available for rollback) and the build cache. Volumes are never pruned.
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

# Chained with && because set -e does not apply inside the `if` conditions that call this.
activate() {
  IMAGE_TAG="$1" compose up -d --force-recreate --remove-orphans && compose restart nginx
}

compose pull

if activate "$IMAGE_TAG" && ./scripts/smoke-prod.sh https://humami.es; then
  echo "[deploy] Running with GHCR_OWNER=${GHCR_OWNER} IMAGE_TAG=${IMAGE_TAG}"
  exit 0
fi

# Automatic rollback: the previous release's images are still on disk because pruning ran before the pull.
if [[ -z "$previous_tag" || "$previous_tag" == "$IMAGE_TAG" ]]; then
  echo "[deploy] Release ${IMAGE_TAG} failed and there is no previous release to roll back to."
  exit 1
fi
# Rollback restores the previous images only; the new docker-compose.images.yml and nginx.conf stay in place.
echo "[deploy] Release ${IMAGE_TAG} failed to activate or pass smoke checks; rolling back to ${previous_tag}."
if activate "$previous_tag" && ./scripts/smoke-prod.sh https://humami.es; then
  echo "[deploy] Rolled back to ${previous_tag}; release ${IMAGE_TAG} is NOT deployed."
else
  echo "[deploy] Rollback to ${previous_tag} also failed (activation or smoke checks); manual recovery needed."
fi
exit 1
