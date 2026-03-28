# GHCR Image Deploy (no source build on server)

## Goal
Deploy Humami on server from prebuilt images in GHCR, avoiding full project rebuilds on the VPS.

## Registry
- `ghcr.io/<owner>/humami-backend:<tag>`
- `ghcr.io/<owner>/humami-frontend:<tag>`

## Production topology
Production assumes:
- backend image
- frontend image
- nginx reverse proxy
- TLS certificates mounted at runtime
- MongoDB hosted externally (Atlas)

`docker-compose.images.yml` is therefore production-oriented and does **not** require a local MongoDB container.

## Local topology
For local development, `docker-compose.yml` may still use a local MongoDB container under the `local` profile.

## One-time server setup
1. Ensure Docker + Compose installed.
2. Keep only runtime files on server (`docker-compose.images.yml`, `.env`, nginx/certbot files, scripts).
3. Keep production secrets outside git (`.env`, registry auth, certs/keys).
4. (Optional but recommended for private images) login to GHCR:

```bash
echo "$GHCR_TOKEN" | docker login ghcr.io -u "$GHCR_USERNAME" --password-stdin
```

## Build and push images (local or builder machine)

```bash
export GHCR_OWNER=huugoo95
export GHCR_USERNAME=huugoo95
export GHCR_TOKEN=<PAT with write:packages>
export IMAGE_TAG=$(git rev-parse --short HEAD)
./scripts/release-build-push.sh
```

Notes:
- The release script is monorepo-aware.
- It builds backend and frontend as separate images.
- It does not require committing credentials anywhere in the repository.
- `latest` is also published by default; disable with `PUBLISH_LATEST=false` if needed.

## Deploy on server

```bash
export GHCR_OWNER=huugoo95
export IMAGE_TAG=<same_tag_or_latest>
./scripts/deploy-images.sh
```

This will:
- pull backend/frontend images from GHCR
- start containers with `docker-compose.images.yml`
- restart nginx
- run smoke checks

## TLS / certificates
Repository policy:
- nginx config and TLS workflow documentation/scripts belong in repo
- real certificates and private keys do **not** belong in repo

Recommended operational model:
- keep certbot data mounted from server storage
- automate renewal + nginx reload outside git-tracked secrets
- document bootstrap/renewal so moving to a new server does not depend on memory
- use `webroot` renewal so the live site does not need to be stopped
- keep `/.well-known/acme-challenge/` mapped to `certbot/www` in nginx

Automation helper:
- `scripts/renew-certs.sh`
- suitable schedule: daily or every 12h

## Notes
- For a 20GB server, image-based deploy reduces disk pressure significantly versus full source builds.
- Keep image retention policy (remove old tags periodically).
- Keep `docker system prune -af` as maintenance when needed.
- Atlas credentials must come from external env/secret injection, never committed to git.
- Frontend lesson learned: browser-side API access must not rely only on `NEXT_PUBLIC_*` runtime expectations, because those values are embedded at image build time. Behind nginx, a relative `/api` fallback is the safe default.
- Production deploy was validated on 2026-03-28 with image tag `f21ec3d` after fixing frontend runtime API resolution.
- Follow-up note: after the validated rollout, keep an eye on browser cache when checking freshly deployed frontend fixes.
