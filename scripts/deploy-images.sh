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

# Every release pulls new SHA-tagged images; reclaim images no container uses (the running
# release and the dev environment keep theirs) and the build cache. Volumes are never pruned.
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

docker compose -f docker-compose.images.yml --profile prod pull

docker compose -f docker-compose.images.yml --profile prod up -d --force-recreate --remove-orphans

docker compose -f docker-compose.images.yml --profile prod restart nginx

if [[ -x "./scripts/smoke-prod.sh" ]]; then
  ./scripts/smoke-prod.sh https://humami.es
fi

echo "[deploy] Running with GHCR_OWNER=${GHCR_OWNER} IMAGE_TAG=${IMAGE_TAG}"
