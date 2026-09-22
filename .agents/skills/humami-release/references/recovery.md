# Recover a failed deployment

Prerequisites: identified previous known-good backend/frontend images, destination and authorization to restore them; data compatibility checked. Application rollback does not undo data changes.

On the approved server with its existing runtime configuration:
1. Set GHCR_OWNER and IMAGE_TAG to the recorded previous release (never latest).
2. Run `docker compose -f docker-compose.images.yml --profile prod pull`.
3. Run `docker compose -f docker-compose.images.yml --profile prod up -d --remove-orphans`.
4. Run `docker compose -f docker-compose.images.yml --profile prod restart nginx`.
5. Run `./scripts/smoke-prod.sh <approved-base-url>` and verify the actual running image identifiers and affected user behavior.

Stop and report if the known-good images are unavailable or incompatible with current data. Record the incident in [incidents](../../../../ops/incidents.md); do not delete data or prune images as part of recovery.
