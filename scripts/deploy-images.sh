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

docker compose -f docker-compose.images.yml --profile prod up -d --remove-orphans

docker compose -f docker-compose.images.yml --profile prod restart nginx

if [[ -x "./scripts/smoke-prod.sh" ]]; then
  tries=0
  until ./scripts/smoke-prod.sh https://humami.es; do
    tries=$((tries + 1))
    if [[ "$tries" -ge 5 ]]; then
      echo "[deploy][ERROR] Smoke checks failed after ${tries} attempts" >&2
      exit 1
    fi
    echo "[deploy] Smoke checks not ready yet, retrying (${tries}/5)..."
    sleep 5
  done
fi

echo "[deploy] Running with GHCR_OWNER=${GHCR_OWNER} IMAGE_TAG=${IMAGE_TAG}"
