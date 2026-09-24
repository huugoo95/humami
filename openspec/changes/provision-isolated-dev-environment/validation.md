# Spec019 progress and verification

## Completed preparation
- User approved deletion of frontend/backend5904567 images and latest aliases. Only those were removed. Disk63%/7GBfree ->55%/8.3GBfree. Active and prior images and all volumes retained.
- Production hostname15.188.51.29,ARM64,1.8GiB RAM,2CPUs; DNS dev.humami.es already points there. Production /,/meals,/api/meals returned200 before operations.
- Definition validated with OpenSpec strict; Compose config and diff checks pass. Independent review corrected potential service alias collisions and missing blog-cover upload block; final candidateb7d3aeb has no pending findings.
- User's brand/logo020 changes incorporated. PR62 merged as26070660011ae18473aa347588bd407fb00be255 after frontend/backend CI passed.
- Runtime directory /home/ubuntu/apps/humami-dev prepared. Independent credentials stored only on server in mode600 .env; no production credentials copied. Baseline image IDs and nginx/Compose backups stored in initial-backup.
- Additive HTTP challenge vhost validated with nginx-t and gracefully reloaded; no production application restarts. Separate dev certificate issued, expires2026-12-23. Existing renewal cron handles both lineages.

## Image build correction
Initial ARM64 run35987499641 failed because backend module context omitted root .mvn wrapper. Correct workflow copies root .mvn and mvnw into checkout module context, matching existing release-build-push.sh behavior. ARM64 image builds now also run on relevant PRs, but publication remains develop-only. No dev containers activated yet; final provenance/health/resources evidence follows after successful publication.

## Publication and registry access (2026-09-24)
- PR63 merged as07554b2d38928d0c1660d139b4684886a0ea0dc1 after independent review and all frontend/backend/ARM64 image checks passed.
- Initial publication attempt failed with write_package. User explicitly authorized Actions Write access for huugoo95/humami on both existing private GHCR packages. After applying that permission, run35988290493 succeeded and published both dev-07554b2d38928d0c1660d139b4684886a0ea0dc1 images.
- Server pull rejected its existing GHCR credential. After user-approved read:packages renewal and temporary SSH use, both images were downloaded. Temporary Docker authentication was removed; existing server credentials were not replaced.
- Production home and recipe API still return200; original802aaee-secfix4 containers remain running. Disk remains55%,8.3GB available.

## Runtime readiness correction
MongoDB remained alive without OOM/restarts, but its5s healthcheck timed out under the0.4CPU cap. An explicit ping succeeded in13.923s. Independent review approved interval30s/timeout30s/start-period60s/retries6; the backend still waits for service_healthy. This changes only dev readiness timing.

## Verified deployment — 2026-09-24 11:01 UTC
- Active URL: https://dev.humami.es. Application source: develop07554b2d38928d0c1660d139b4684886a0ea0dc1, including brand/logo020. Both image architecture and revision labels were checked before activation.
- Backend digest: sha256:209f40fe5b8b37085a4280deab9db6965c927b99189e7d677d997d2d9a1bde46.
- Frontend digest: sha256:ff983a0eece229304d739aead2bd7b234e9e5c9b28efaec3fdf717af981587f3.
- Runtime: /home/ubuntu/apps/humami-dev. Compose uses docker-compose.dev.yml plus docker-compose.dev.pinned.yml. Secret-free deployment.json records source, digests, limits and verification; .env remains mode600.
- Valid TLS; /, /meals and /api/meals?query=&page=1&limit=1 return200 on both dev and production. Dev returns items:[],totalItems:0. Unauthorized recipe POST returns401. All three image upload routes return503. robots.txt disallows indexing and dev responses include X-Robots-Tag.
- Dev backend targets dev-mongo/humami_dev and uses disabled S3 credentials. No dev application/database ports are published. All three dev containers run with zero restarts and no OOM.
- Production backend/frontend/nginx container IDs and image IDs exactly match initial-backup/production-images.json. Only additive proxy routing/network membership was changed; nginx config validation passed before graceful reload. Persisted proxy Compose includes the external dev ingress network.
- Final disk:19GB total,13GB used,5.5GB available (71%). RAM:1839MiB total,429MiB available at verification. Backend initial startup took183s; observed host CPU steal54–67% contributes to slow starts. Suitable for light development; reassess shared capacity before load testing.
- Credential limitation: the old server GHCR credential still cannot pull current private packages. This deployment used explicitly approved temporary authentication, removed afterward; future downloads need renewed authorization or a dedicated read-only deployment credential. Existing pinned images can restart locally without registry access.
- Recovery: stop only the humami-dev Compose project (retain its volume), restore initial-backup/nginx.conf in place, validate/reload nginx; restore the saved production Compose and detach humami-dev-ingress from the proxy. Production image rollback is unnecessary because application containers were never replaced.
- Archive remains pending user acceptance.
