#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

: "${GHCR_OWNER:=huugoo95}"
: "${IMAGE_TAG:=$(git rev-parse --short HEAD)}"
: "${PLATFORM:=linux/amd64}"

if [[ -z "${GHCR_TOKEN:-}" ]]; then
  echo "[release][ERROR] GHCR_TOKEN is required (PAT with write:packages)" >&2
  exit 1
fi

if [[ -z "${GHCR_USERNAME:-}" ]]; then
  GHCR_USERNAME="$GHCR_OWNER"
fi

echo "$GHCR_TOKEN" | docker login ghcr.io -u "$GHCR_USERNAME" --password-stdin

echo "[release] Building and pushing backend -> ghcr.io/${GHCR_OWNER}/humami-backend:${IMAGE_TAG}"
docker buildx build \
  --platform "$PLATFORM" \
  -t "ghcr.io/${GHCR_OWNER}/humami-backend:${IMAGE_TAG}" \
  -t "ghcr.io/${GHCR_OWNER}/humami-backend:latest" \
  --push \
  ./humami-backend

echo "[release] Building and pushing frontend -> ghcr.io/${GHCR_OWNER}/humami-frontend:${IMAGE_TAG}"
docker buildx build \
  --platform "$PLATFORM" \
  -t "ghcr.io/${GHCR_OWNER}/humami-frontend:${IMAGE_TAG}" \
  -t "ghcr.io/${GHCR_OWNER}/humami-frontend:latest" \
  --push \
  ./humami-web

echo "[release] Done. Published tag: ${IMAGE_TAG}"
