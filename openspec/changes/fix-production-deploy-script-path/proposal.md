# Spec 031 — Run the deployed production scripts

## Why

The production deployment workflow copies `scripts/deploy-images.sh` and `scripts/smoke-prod.sh` into `/home/ubuntu/apps/humami/`, but it runs `./scripts/deploy-images.sh` from that directory. The server therefore keeps running stale copies under `scripts/`. As a result, spec 028's 480-second readiness window never reached production: releases 0.5.1 and 0.5.2 both failed their deploy job after a 90-second wait, while the backend took about 3 minutes to start (incident 2026-10-18 in `ops/incidents.md`).

## Scope

- Copy both scripts into `/home/ubuntu/apps/humami/scripts/`, where `deploy-images.sh` expects to live (it resolves the app root as its parent directory).
- Keep `docker-compose.images.yml` and `nginx.conf` in the app root.
- Before deploying, check that the remote scripts match the checked-out revision, so a path mismatch cannot run stale scripts silently again.

No change to images, Compose services, nginx configuration or smoke-check endpoints.

## Acceptance criteria

- The deployment job runs the `deploy-images.sh` and `smoke-prod.sh` from the commit being deployed.
- The job fails before recreating containers if the remote scripts differ from the checked-out ones.
- The readiness wait uses the repository default (480 s) unless it is overridden explicitly.
