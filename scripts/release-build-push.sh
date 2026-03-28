#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

: "${GHCR_OWNER:=huugoo95}"
: "${IMAGE_TAG:=$(git rev-parse --short HEAD)}"
: "${PLATFORM:=linux/amd64}"
: "${PUBLISH_LATEST:=true}"

if [[ -z "${GHCR_TOKEN:-}" ]]; then
  echo "[release][ERROR] GHCR_TOKEN is required (PAT with write:packages)" >&2
  exit 1
fi

if [[ -z "${GHCR_USERNAME:-}" ]]; then
  GHCR_USERNAME="$GHCR_OWNER"
fi

BACKEND_IMAGE="ghcr.io/${GHCR_OWNER}/humami-backend"
FRONTEND_IMAGE="ghcr.io/${GHCR_OWNER}/humami-frontend"

publish_tag() {
  local image="$1"
  local tag="$2"
  echo "[release] Pushing ${image}:${tag}"
  docker push "${image}:${tag}"
}

build_backend() {
  local backend_ctx
  backend_ctx="$(mktemp -d)"
  trap 'rm -rf "$backend_ctx"' RETURN

  cp -r "$ROOT_DIR/.mvn" "$backend_ctx/.mvn"
  cp "$ROOT_DIR/mvnw" "$backend_ctx/mvnw"
  cp "$ROOT_DIR/humami-backend/pom.xml" "$backend_ctx/pom.xml"
  cp -r "$ROOT_DIR/humami-backend/src" "$backend_ctx/src"
  cp "$ROOT_DIR/humami-backend/Dockerfile" "$backend_ctx/Dockerfile"

  echo "[release] Building backend -> ${BACKEND_IMAGE}:${IMAGE_TAG}"
  docker build \
    -t "${BACKEND_IMAGE}:${IMAGE_TAG}" \
    "$backend_ctx"

  if [[ "$PUBLISH_LATEST" == "true" ]]; then
    docker tag "${BACKEND_IMAGE}:${IMAGE_TAG}" "${BACKEND_IMAGE}:latest"
  fi
}

build_frontend() {
  echo "[release] Building frontend -> ${FRONTEND_IMAGE}:${IMAGE_TAG}"
  docker build \
    -t "${FRONTEND_IMAGE}:${IMAGE_TAG}" \
    "$ROOT_DIR/humami-web"

  if [[ "$PUBLISH_LATEST" == "true" ]]; then
    docker tag "${FRONTEND_IMAGE}:${IMAGE_TAG}" "${FRONTEND_IMAGE}:latest"
  fi
}

echo "$GHCR_TOKEN" | docker login ghcr.io -u "$GHCR_USERNAME" --password-stdin

build_backend
build_frontend

publish_tag "$BACKEND_IMAGE" "$IMAGE_TAG"
publish_tag "$FRONTEND_IMAGE" "$IMAGE_TAG"

if [[ "$PUBLISH_LATEST" == "true" ]]; then
  publish_tag "$BACKEND_IMAGE" latest
  publish_tag "$FRONTEND_IMAGE" latest
fi

echo "[release] Done. Published tag: ${IMAGE_TAG}"
