# Deployment architecture

Production consists of separate backend/frontend GHCR images, nginx/TLS and external MongoDB Atlas. Local development may use MongoDB from docker-compose.yml; docker-compose.images.yml is the production runtime topology.

Images: `ghcr.io/<owner>/humami-backend:<tag>` and `ghcr.io/<owner>/humami-frontend:<tag>`. Certificates, private keys, registry authentication and Atlas credentials are runtime inputs kept outside Git.

Production releases originate exclusively from master. Integration into develop is not deployment. The canonical [release procedure](../.agents/skills/humami-release/SKILL.md) owns build/push/deploy/verification; its recovery reference owns rollback steps.

## Existing tooling and limitations
- release-build-push.sh builds both images and pushes tags; it also publishes latest by default. The release skill must supply an explicit version and disable latest publication.
- deploy-images.sh defaults to latest and hardcodes humami.es smoke verification. These are script behaviors, not permission to use latest or another target in production.
- PLATFORM is declared by the build script but not applied to docker build; do not assume cross-platform images.
- Frontend browser API resolution uses a relative /api fallback because NEXT_PUBLIC variables may be embedded at build time.
- scripts/renew-certs.sh supports webroot renewal. Host scheduling and certificate state must be verified on the actual server; the repository does not establish that a schedule is installed.
- Historical image rollout and smoke success on 2026-03-28 do not establish the current running version.
