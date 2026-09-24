# Design

## Data isolation and credentials

Atlas hosts two databases: production `humami` and development `humami_dev`. The migration operator may authenticate with the existing `atlasAdmin` production account only for the one-time create/dump/restore sequence. A new random-password user is created in Atlas with `readWrite` solely on `humami_dev`; it is the only database URI persisted in `/home/ubuntu/apps/humami-dev/.env` for the dev backend. The password is never placed in Git, logs, OpenSpec evidence or command output.

The existing Atlas network policy already allows the shared host because production connects from it. No new public network rule is needed.

## Migration sequence

1. Capture a fresh dump of the local `humami_dev` data and inventory collection counts/index definitions.
2. Create the dedicated Atlas user and restore the dump into Atlas `humami_dev` through that new user.
3. Compare the Atlas collection inventory with the local source. Production `humami` is read-only throughout; no collection is dropped or restored there.
4. Update the dev Compose configuration so `dev-backend` requires `DEV_ATLAS_MONGODB_URI`, has no `depends_on` local MongoDB, and is attached only to the existing ingress network.
5. Restart only dev backend, verify dev/public endpoints and production container identities. Then stop the local MongoDB container while retaining its volume and server-side backup.

## Recovery

Before changing the backend, preserve the local dev Compose/runtime configuration and local database volume. If Atlas connectivity, inventory comparison or dev endpoint verification fails, restore the previous dev URI/configuration and restart the local backend; do not remove the local Mongo container or volume. Removing the local fallback is a separate, explicitly requested cleanup operation.

## Verification

Verify the dev backend's configured database name and Atlas host without displaying credentials. Verify recipe and About endpoint counts against the pre-migration inventory, unauthorized write behaviour, disabled image upload routes, TLS/noindex and absence of dev database/application host ports. Verify production home/catalogue/API and original production container image IDs. Record only redacted endpoint, count and digest evidence.
