#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

COMPOSE="docker compose -f docker-compose.images.yml --profile prod"
LOG_PREFIX="[cert-renew]"

${COMPOSE} run --rm certbot renew --webroot -w /var/www/certbot

# nginx serves the certificates from the mounted letsencrypt volume.
# Reload is enough and avoids a full container recreation.
${COMPOSE} exec -T nginx nginx -s reload || ${COMPOSE} restart nginx

echo "${LOG_PREFIX} Renewal check finished successfully"
