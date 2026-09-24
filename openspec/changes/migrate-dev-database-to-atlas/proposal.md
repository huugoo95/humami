# Spec 021 — Atlas-backed development database

## Why and goal

The current development database runs in a MongoDB container on the shared application host. The owner requested moving it beside production in MongoDB Atlas. The host has limited memory and disk, and the current dev catalogue has already been populated from production.

Priority: Now, requested infrastructure change. As the owner, I want dev data to live in Atlas without allowing the dev runtime to operate with production database credentials. Success: `dev.humami.es` reads the migrated `humami_dev` data through a dedicated Atlas user, the local Mongo service is stopped but recoverable, and production health and data remain unchanged. Deadline not provided.

## What changes

- Create the `humami_dev` database in the existing Atlas cluster, using a dedicated application user scoped to that database only.
- Migrate the current dev database with a verified dump/restore and retain a recoverable local backup.
- Change the dev backend runtime to use the dedicated Atlas URI; remove its dependency on the local Mongo service.
- Stop, but do not delete, the local dev MongoDB container and its volume after Atlas verification.

## Constraints and non-goals

- Production database `humami`, its application user, production containers and images are out of scope.
- The existing production Atlas credential is only a temporary migration operator and MUST NOT be added to the dev runtime configuration.
- Dev remains private from search engines, uses its independent write secret and continues to disable image uploads until separate object storage exists.
- This is not a production release and does not deploy application code from `develop`.

## Capabilities

### New capabilities

- `atlas-dev-database`: Atlas-backed, credential-isolated persistence for the existing development runtime.

### Modified capabilities

- `dev-environment`: its development data store moves from host-local MongoDB to the existing Atlas cluster.
