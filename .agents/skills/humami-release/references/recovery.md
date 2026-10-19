# Recover a failed deployment

The deploy script already rolls back automatically when smoke checks fail. Use this only when that did not work.

Preferred, no host access needed:
- Revert the bad merge on `master` with a PR. The workflow then deploys the reverted SHA.
- Or, if a fix is quick, merge the fix.

If the workflow itself cannot deploy (for example disk, registry or SSH problems), the owner needs host access. On `/home/ubuntu/apps/humami`, run these steps in order:
1. Log in to GHCR with a `read:packages` token (`docker login ghcr.io`).
2. Set `GHCR_OWNER` and `IMAGE_TAG` to a known-good `master` SHA (never `latest`).
3. Run `docker compose -f docker-compose.images.yml --profile prod pull`.
4. Run `docker compose -f docker-compose.images.yml --profile prod up -d --force-recreate --remove-orphans`, then `restart nginx`.
5. Run `./scripts/smoke-prod.sh https://humami.es`.

Application rollback does not undo data changes. Record the incident in [incidents](../../../../ops/incidents.md).
