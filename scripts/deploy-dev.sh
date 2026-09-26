#!/usr/bin/env bash
# Runs on the DEV host. The SSH wrapper supplies a one-use GHCR read token in
# DEV_GHCR_READ_TOKEN and leaves stdin available for this script.
set -euo pipefail

app_dir="${HUMAMI_DEV_APP_DIR:-/home/ubuntu/apps/humami-dev}"
compose_file="$app_dir/docker-compose.dev.yml"
pinned_file="$app_dir/docker-compose.dev.pinned.yml"
record_file="$app_dir/deployment.json"
backup_dir="$(mktemp -d "$app_dir/.deploy-backup.XXXXXX")"
compose_changed=false
pins_changed=false
verified=false
had_pinned_file=false

cleanup() {
  unset ghcr_token
  docker logout ghcr.io >/dev/null 2>&1 || true
  rm -rf "$backup_dir"
}
restore_on_failure() {
  status="$1"
  if [[ "$status" -ne 0 && "$verified" != true ]]; then
    if [[ "$pins_changed" == true ]]; then
      if [[ "$had_pinned_file" == true ]]; then cp "$backup_dir/pinned" "$pinned_file"; else rm -f "$pinned_file"; fi
    fi
    if [[ "$compose_changed" == true ]]; then cp "$backup_dir/compose" "$compose_file"; fi
    if [[ "$pins_changed" == true || "$compose_changed" == true ]]; then
      docker compose -f "$compose_file" -f "$pinned_file" config -q >/dev/null 2>&1 && \
        docker compose -f "$compose_file" -f "$pinned_file" up -d --no-build --no-deps dev-backend dev-frontend >/dev/null 2>&1 || true
    fi
  fi
}
finish() {
  status="$?"
  restore_on_failure "$status"
  cleanup
  exit "$status"
}
trap finish EXIT

ghcr_token="${DEV_GHCR_READ_TOKEN:?DEV_GHCR_READ_TOKEN is required}"
unset DEV_GHCR_READ_TOKEN
source_sha="${1:-}"
if [[ ! "$source_sha" =~ ^[0-9a-f]{40}$ ]]; then
  echo "source SHA must be a full lowercase hexadecimal commit SHA" >&2
  exit 2
fi
backend_tag="ghcr.io/huugoo95/humami-backend:dev-$source_sha"
frontend_tag="ghcr.io/huugoo95/humami-frontend:dev-$source_sha"

cd "$app_dir"
if [[ -f "$compose_file.next" ]]; then
  validation_pin="$pinned_file"
  if [[ ! -f "$validation_pin" ]]; then
    validation_pin="$backup_dir/empty-pinned.yml"
    printf 'services: {}\n' > "$validation_pin"
  fi
  docker compose -f "$compose_file.next" -f "$validation_pin" config -q
  cp "$compose_file" "$backup_dir/compose"
  mv "$compose_file.next" "$compose_file"
  compose_changed=true
fi

printf '%s' "$ghcr_token" | docker login ghcr.io --username huugoo95 --password-stdin >/dev/null
docker pull "$backend_tag" >/dev/null
docker pull "$frontend_tag" >/dev/null
docker logout ghcr.io >/dev/null 2>&1 || true
unset ghcr_token

backend_digest="$(docker image inspect --format '{{index .RepoDigests 0}}' "$backend_tag")"
frontend_digest="$(docker image inspect --format '{{index .RepoDigests 0}}' "$frontend_tag")"
if [[ -f "$pinned_file" ]]; then
  cp "$pinned_file" "$backup_dir/pinned"
  had_pinned_file=true
fi
cat > "$pinned_file.next" <<PINNED
services:
  dev-backend:
    image: $backend_digest
  dev-frontend:
    image: $frontend_digest
PINNED
mv "$pinned_file.next" "$pinned_file"
pins_changed=true

docker compose -f "$compose_file" -f "$pinned_file" config -q
docker compose -f "$compose_file" -f "$pinned_file" up -d --no-build --no-deps dev-backend dev-frontend

health_url="${HUMAMI_DEV_URL:-https://dev.humami.es}"
health_attempts="${HUMAMI_DEV_HEALTH_ATTEMPTS:-72}"
for attempt in $(seq 1 "$health_attempts"); do
  home_code="$(curl -fsS -o /dev/null -w '%{http_code}' "$health_url/" || true)"
  meals_code="$(curl -fsS -o /dev/null -w '%{http_code}' "$health_url/meals" || true)"
  guides_code="$(curl -fsS -o /dev/null -w '%{http_code}' "$health_url/api/guides" || true)"
  if [[ "$home_code" == 200 && "$meals_code" == 200 && "$guides_code" == 200 ]]; then
    verified_at="$(date -u +%Y-%m-%dT%H:%M:%SZ)"
    cat > "$record_file.next" <<RECORD
{
  "sourceSha": "$source_sha",
  "status": "verified",
  "verifiedAt": "$verified_at",
  "images": {
    "dev-backend": "$backend_digest",
    "dev-frontend": "$frontend_digest"
  },
  "verification": {
    "dev.humami.es/": $home_code,
    "dev.humami.es/meals": $meals_code,
    "dev.humami.es/api/guides": $guides_code
  }
}
RECORD
    chmod 600 "$record_file.next"
    mv "$record_file.next" "$record_file"
    verified=true
    printf 'DEV deployment verified: %s\n' "$source_sha"
    exit 0
  fi
  sleep 5
done

docker compose -f "$compose_file" -f "$pinned_file" ps || true
docker logs --tail 120 humami-dev-backend || true
echo "DEV health check timed out: home=$home_code meals=$meals_code guides=$guides_code" >&2
exit 1
