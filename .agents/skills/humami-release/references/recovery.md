# Recover a failed deployment

The deploy script already rolls back automatically when smoke checks fail. Use this only when that did not work.

Preferred, no host access needed:
- Revert the bad merge on `master` with a PR. The workflow then deploys the reverted SHA.
- Or, if a fix is quick, merge the fix.

If the workflow itself cannot deploy (for example disk, registry or SSH problems), the owner needs host access. On `/home/ubuntu/apps/humami`:
1. Log in to GHCR with a `read:packages` token (`docker login ghcr.io`).
2. Run `./scripts/deploy-images.sh --status` to see the running tags, and `cat release.state` for the active slots.
3. Redeploy known-good `master` SHAs (never `latest`): `BACKEND_TAG=<sha> FRONTEND_TAG=<sha> ./scripts/deploy-images.sh`. It starts them next to the running containers and switches nginx only when they are healthy.
4. If nginx itself is down, `docker compose -f docker-compose.images.yml --profile prod up -d --no-deps nginx` (the rendered `nginx.conf` must point at running slots).

Application rollback does not undo data changes. Record the incident in [incidents](../../../../ops/incidents.md).
