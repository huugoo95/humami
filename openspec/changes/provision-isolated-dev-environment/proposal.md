# Spec 019 — Isolated development environment

## Why and goal
The user requests removing unused images and hosting a development environment at dev.humami.es on the existing production server. DNS resolves to15.188.51.29. The ARM64 host has1.8GiB RAM,2 CPUs and19GB disk; production must remain available.
Priority: Now, requested infrastructure work. As the owner, I want to test develop separately without writing production data. Success: HTTPS homepage, catalogue and API respond on dev; production responses and image IDs remain unchanged; database/secret/storage are not shared; resource caps are applied. Deadline not provided.

## What Changes
- Remove only explicitly approved obsolete5904567 frontend/backend images and latest aliases, retaining active and prior rollback images and all volumes.
- Define separate dev Compose project, local empty authenticated MongoDB, independent write secret and persistent dev volume. No production data copied.
- Build ARM64 images off-host from a verified develop commit, with dev-prefixed immutable tags; no latest overwrite and no production deployment.
- Add nginx virtual host and separate Let's Encrypt certificate; existing renewal covers both lineages. Keep production upstreams and certificates unchanged.
- Apply memory/CPU/log caps and noindex headers. Until separate image storage exists, reject dev image-upload routes and use no real S3 credentials.

## Capabilities
### New Capabilities
- `dev-environment`: isolated, resource-bounded development runtime on the shared host.
### Modified Capabilities
None. Production release still requires master. No application feature or data migration.

## Constraints
Default is empty local dev database; user was asked about separate services and can supply them instead. S3 bucket is currently hardcoded in code, so copying production S3 credentials is prohibited. GitHub-hosted ARM64 builds avoid exhausting production RAM. Image publication is authorized as necessary for requested deployment; no automatic production deployment or DNS mutation.
