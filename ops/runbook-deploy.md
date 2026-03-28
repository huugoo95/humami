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
4. `docker compose -f docker-compose.images.yml --profile prod up -d`
5. `docker compose -f docker-compose.images.yml --profile prod restart nginx`
6. Re-run smoke checks

## TLS / HTTPS notes
- nginx config should be versioned in repo
- TLS private keys/certificates must not be versioned in repo
- renewal/reload workflow must be documented and repeatable on a fresh server
- avoid server-only "magic" changes that are not captured in repo docs/scripts

## Notes
- First deploy after image/cache changes can take longer.
- Keep incident notes in `ops/incidents.md`.
- Atlas credentials and production secrets must be injected externally and never committed.
