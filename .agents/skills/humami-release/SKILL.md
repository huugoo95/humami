---
name: humami-release
description: Follow, verify and recover Humami production deployments. Every merge to master deploys automatically; this covers watching that deploy, verifying production and recovering from failures.
---

# Deploy, verify and recover production

## How deployment works
- A push to `master` runs `.github/workflows/production-deploy.yml`. It builds ARM64 images tagged with the exact commit SHA and copies the Compose file, nginx config and scripts to the host, verifying their checksums. It then runs `scripts/deploy-images.sh`.
- The deploy script:
  - records the running release;
  - prunes unused images and checks free disk space;
  - pulls the new images, recreates the containers and runs `scripts/smoke-prod.sh`.
- **If smoke checks fail, it rolls back automatically** to the previous release (its images are still on disk) and the job fails. Read the log: "Rolled back to …" means production runs the previous release and the new one is not deployed.
- Deploys are serialized. The production host has about 19 GB of disk and a t4g.small CPU with burst credits; backend start can take several minutes.

## After a merge
1. Wait for the production workflow run for the merge SHA (in the background, not a tight polling loop).
2. On success, verify the affected behaviour on https://humami.es: the relevant pages and API endpoints, plus a browser check when visible. Report anything not verified.
3. On failure, read the failed log. If it rolled back, fix forward with a new PR or revert the merge. If the rollback also failed or the failure happened before activation (disk, pull, checksum), follow [recovery](references/recovery.md).

## Rules
- Never deploy `latest` or an untracked image; production always runs an exact `master` SHA.
- Data changes are not rolled back by image rollback: design migrations to be backward compatible.
- Record incidents in `ops/incidents.md`.
