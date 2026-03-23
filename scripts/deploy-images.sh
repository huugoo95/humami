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

docker compose -f docker-compose.images.yml --profile prod pull

docker compose -f docker-compose.images.yml --profile prod up -d

docker compose -f docker-compose.images.yml --profile prod restart nginx

if [[ -x "./scripts/smoke-prod.sh" ]]; then
  ./scripts/smoke-prod.sh https://humami.es
fi

echo "[deploy] Running with GHCR_OWNER=${GHCR_OWNER} IMAGE_TAG=${IMAGE_TAG}"
