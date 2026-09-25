# Design

## Runtime
Store dev runtime in /home/ubuntu/apps/humami-dev, Compose project humami-dev. MongoDB8.0.15 on an internal-only network, separate volume/auth, no host port. Backend and frontend use a dev ingress network connected to the existing nginx container. Unique aliases avoid production backend/frontend DNS collisions. The proxy network connection must be retained in the server's nginx Compose configuration for later recreation.

Cap backend at384MiB (heap192MiB), frontend192MiB (Node heap128MiB), MongoDB384MiB (WiredTiger cache0.25GiB; monitor actual fit and adjust caps if startup cannot fit). CPU quotas sum<=1.5cores; production has no new limits. Limit Docker log rotation. Do not build on the server or add swap without a demonstrated need.

## Images and frontend API
CI builds ARM64 images off-host and publishes dev-SHA tags after develop integration. Docker frontend receives an optional build argument for https://dev.humami.es/api so server rendering and browser requests both reach dev; production builds preserve existing default. Image SHA/provenance and architecture are verified before runtime activation. No latest tags.

## Proxy and TLS
Add only dev HTTP/HTTPS server blocks to a backed-up existing nginx config. HTTP challenge shares existing certbot webroot. Issue a separate dev certificate with the existing account; existing renew checks all certificates. Validate nginx config before graceful reload; restore previous file on validation failure. Resolve dev upstream names dynamically so dev restarts do not block production nginx reloads. Block image upload routes with503 until separate storage is configured; dev robots disallow all and X-Robots-Tag noindex. Public read access follows current app behavior; writes require an independent generated secret kept on server mode600.

## Verification and recovery
Record running production image IDs before changes; check home/catalogue/API/TLS before and after. Verify Mongo URI/database is dev-only without printing credentials; unauthorized writes fail. Check container caps, readiness, disk and memory after startup. Stop only dev services to recover; do not remove volumes. Remove dev server blocks/network connection only after nginx validation and restore backed-up config if necessary. No data migration or production version change.
