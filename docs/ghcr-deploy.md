# GHCR Image Deploy (no source build on server)

## Goal
Deploy Humami on server from prebuilt images in GHCR, avoiding full project rebuilds on the VPS.

## Registry
- `ghcr.io/<owner>/humami-backend:<tag>`
- `ghcr.io/<owner>/humami-frontend:<tag>`

## One-time server setup
1. Ensure Docker + Compose installed.
2. Keep only runtime files on server (`docker-compose.images.yml`, `.env`, nginx/certbot files, scripts).
3. (Optional but recommended for private images) login to GHCR:

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

## Notes
- For a 20GB server, this model reduces disk pressure significantly.
- Keep image retention policy (remove old tags periodically).
- Keep `docker system prune -af` as maintenance when needed.
