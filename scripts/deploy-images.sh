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

report_disk() {
  echo "[deploy] Disk usage ($1):"
  df -h / || true
  docker system df || true
}

# Every release pulls new SHA-tagged images; reclaim images no container uses (the running
# release and the dev environment keep theirs) and the build cache. Volumes are never pruned.
report_disk "before cleanup"
docker image prune -af
docker builder prune -af
report_disk "after cleanup"

docker_root="$(docker info --format '{{.DockerRootDir}}')"
free_mb="$(df -Pm "$docker_root" | awk 'NR==2 {print $4}')"
if (( free_mb < MIN_FREE_MB )); then
  echo "[deploy] Only ${free_mb} MB free under ${docker_root}; ${MIN_FREE_MB} MB required. Aborting before pull."
  exit 1
fi
echo "[deploy] ${free_mb} MB free under ${docker_root}"

docker compose -f docker-compose.images.yml --profile prod pull

docker compose -f docker-compose.images.yml --profile prod up -d --force-recreate --remove-orphans

docker compose -f docker-compose.images.yml --profile prod restart nginx

if [[ -x "./scripts/smoke-prod.sh" ]]; then
  ./scripts/smoke-prod.sh https://humami.es
fi

echo "[deploy] Running with GHCR_OWNER=${GHCR_OWNER} IMAGE_TAG=${IMAGE_TAG}"
