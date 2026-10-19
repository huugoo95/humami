---
name: humami-release
description: Follow, verify and recover Humami production deployments. Every merge to master deploys automatically; this covers watching that deploy, verifying production and recovering from failures.
---

# Deploy, verify and recover production

## How deployment works
- A push to `master` runs `.github/workflows/production-deploy.yml`. It builds ARM64 images tagged with the exact commit SHA and copies the Compose file, nginx config and scripts to the host, verifying their checksums. It then runs `scripts/deploy-images.sh`.
- The workflow first reads the release running on the host and builds only the images whose module changed (`humami-backend/**` with `.mvn`/`mvnw`, `humami-web/**`, or the workflow itself); an unchanged service keeps its running tag.
- The deploy script runs backend and frontend in blue/green slots:
  - a service whose tag, Compose definition or `.env` changed is started in the idle slot next to the running one; unchanged services are not touched;
  - once the new containers answer locally, it renders `nginx.conf` (upstreams + `nginx/site.conf`, the repository's `nginx.conf`) and reloads nginx, then runs `scripts/smoke-prod.sh`;
  - only after the smoke checks pass does it record the slots in `release.state` and remove the old containers. The rendered `nginx.conf` is the source of truth for which slot is live; the host keeps a `deploy.log`.
- **If the new containers never get ready, nginx rejects the config, or smoke checks fail, the old containers are still running and nginx is switched back to them**; the job fails and production keeps the previous release. "Rolled back" or "production still runs the previous release" in the log means the new one is not deployed. The new Compose file and `nginx/site.conf` stay on the host, so a broken config change needs a revert.
- Two JVMs run side by side for a few minutes. With less than 450 MB available (550 MB when the frontend also changes) the script stops the old backend first (an /api outage for that deploy) and says so in the log.
- Deploys are serialized. The production host has about 19 GB of disk and a t4g.small CPU with burst credits; backend start can take several minutes.

## After a merge
1. Wait for the production workflow run for the merge SHA (in the background, not a tight polling loop).
2. On success, verify the affected behaviour on https://humami.es: the relevant pages and API endpoints, plus a browser check when visible. Report anything not verified.
3. On failure, read the failed log. If it rolled back, fix forward with a new PR or revert the merge. If the rollback also failed or the failure happened before activation (disk, pull, checksum), follow [recovery](references/recovery.md).

## Rules
- Never deploy `latest` or an untracked image; production always runs an exact `master` SHA.
- Data changes are not rolled back by image rollback: design migrations to be backward compatible.
- Record incidents in `ops/incidents.md`.
