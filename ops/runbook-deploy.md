# Runbook: Deploy (Humami)

## Scope
Production deploy on `ubuntu@humami.es` for repository `/home/ubuntu/apps/humami`.

## Production assumptions
- MongoDB is hosted externally (Atlas)
- production server does not need a local MongoDB container
- backend and frontend are deployed from separate GHCR images
- nginx terminates HTTP/HTTPS in front of the app containers
- production secrets/certs stay outside git

## Preconditions
- Changes merged to target branch (`develop` currently)
- SSH access available
- No active incident blocking deploy
- Production `.env` present on server
- TLS cert/key material present on server if nginx is serving HTTPS directly

## Standard deploy (legacy / source-build)
This path may remain useful for debugging, but production should prefer image-based deploy.

1. Connect and update code:
   - `cd /home/ubuntu/apps/humami`
   - `git fetch origin`
   - `git checkout develop`
   - `git pull --ff-only origin develop`
2. Build and start containers:
   - `docker compose --profile local up -d --build`
3. If nginx is part of the target environment, restart it to refresh upstream targets:
   - `docker compose --profile prod restart nginx`
4. Run smoke checks:
   - `./scripts/smoke-prod.sh https://humami.es`

## Optimized deploy (GHCR images, recommended)
Use prebuilt images instead of building full source on the server.

### Validated rollout notes (2026-03-28)
- deployed successfully on `humami.es` using image-based deploy
- validated tags during rollout included:
  - `50c4169`
  - `f21ec3d` (frontend runtime API resolution fix)
- smoke checks passed after the image-based rollout
- nginx restart may create a short readiness gap, so deploy script now retries smoke checks
- browser-side frontend API calls must keep a relative `/api` fallback; relying only on `NEXT_PUBLIC_*` values baked into the image caused the meals page to show no recipes even though backend/API were healthy
- an old `humami-mongo` container may still be present from previous deployments; production runtime no longer depends on it when Atlas is configured

1. Build and push images from local/builder machine:
   - `./scripts/release-build-push.sh`
2. On server, deploy from image tags:
   - `./scripts/deploy-images.sh`

Reference: `docs/ghcr-deploy.md`

## Success criteria
- `/` => 200
- `/meals` => 200
- `/api/meals?query=&page=1&limit=5` => 200

## Fast rollback (manual)
1. Identify last known good image tag
2. Set `IMAGE_TAG=<good-tag>`
3. `docker compose -f docker-compose.images.yml --profile prod pull`
4. `docker compose -f docker-compose.images.yml --profile prod up -d --remove-orphans`
5. `docker compose -f docker-compose.images.yml --profile prod restart nginx`
6. Re-run smoke checks

## TLS / HTTPS notes
- nginx config should be versioned in repo
- TLS private keys/certificates must not be versioned in repo
- renewal/reload workflow must be documented and repeatable on a fresh server
- avoid server-only "magic" changes that are not captured in repo docs/scripts
- `/.well-known/acme-challenge/` must be served by nginx from `certbot/www` on the live site; this was validated during SSL automation setup
- preferred renewal mode is `webroot`, so frontend/nginx do not need to be stopped during renewal

### Renewal automation
- script: `scripts/renew-certs.sh`
- recommended cadence: daily (or every 12h if you want extra margin)
- current recommendation for Humami: daily cron on the host

## Notes
- First deploy after image/cache changes can take longer.
- Keep incident notes in `ops/incidents.md`.
- Atlas credentials and production secrets must be injected externally and never committed.
