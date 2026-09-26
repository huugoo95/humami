# Deployment architecture

Production consists of separate backend/frontend GHCR images, nginx/TLS and external MongoDB Atlas. Local development may use MongoDB from docker-compose.yml; docker-compose.images.yml is the production runtime topology.

Images: `ghcr.io/<owner>/humami-backend:<tag>` and `ghcr.io/<owner>/humami-frontend:<tag>`. Certificates, private keys, registry authentication and Atlas credentials are runtime inputs kept outside Git.

Production releases originate exclusively from master. Integration into develop is not deployment. The canonical [release procedure](../.agents/skills/humami-release/SKILL.md) owns build/push/deploy/verification; its recovery reference owns rollback steps.

The automated [production workflow](../.github/workflows/production-deploy.yml) validates ARM64 builds for pull requests to `master`, then publishes and deploys the full master SHA only after merge. It uses `GITHUB_TOKEN` for package publication, synchronizes the versioned Compose and smoke/deployment scripts to the runtime, and uses no `latest` tag. The deploy step requires repository secrets `PROD_DEPLOY_HOST`, `PROD_DEPLOY_USER`, `PROD_DEPLOY_SSH_KEY`, `PROD_DEPLOY_KNOWN_HOSTS` and a classic `PROD_GHCR_READ_TOKEN` limited to `read:packages`; it streams that read token to the server for the deployment only and logs out afterwards. Production runs are serialized. Recovery remains an explicit versioned release operation rather than an arbitrary workflow dispatch.

## Existing tooling and limitations
- release-build-push.sh builds both images and pushes tags; it also publishes latest by default. The release skill must supply an explicit version and disable latest publication.
- deploy-images.sh defaults to latest and hardcodes humami.es smoke verification. These are script behaviors, not permission to use latest or another target in production.
- PLATFORM is declared by the build script but not applied to docker build; do not assume cross-platform images.
- Frontend browser API resolution uses a relative /api fallback because NEXT_PUBLIC variables may be embedded at build time.
- scripts/renew-certs.sh supports webroot renewal. Host scheduling and certificate state must be verified on the actual server; the repository does not establish that a schedule is installed.
- Historical image rollout and smoke success on 2026-03-28 do not establish the current running version.

## Runtime configuration and operating context

The recorded production host is ubuntu@humami.es with runtime files under /home/ubuntu/apps/humami. Treat these as deployment context to confirm before an operation, not evidence of current state. Keep docker-compose.images.yml, nginx/certbot configuration and scripts on the host; secrets are supplied by its external .env or equivalent runtime injection. Private GHCR images require registry access.

Nginx terminates TLS using mounted certificate storage. The webroot challenge location /.well-known/acme-challenge/ maps to certbot/www; renewal must preserve that mapping and reload nginx without stopping the site. The documented scheduling recommendation is daily (or every 12 hours); scripts/renew-certs.sh exists, but installed scheduling must be verified on the host.

Image-based deployment was chosen to reduce disk pressure on the recorded 20GB server. Retain known-good release images for recovery; image cleanup is separate maintenance, never an implicit release step. A legacy local Mongo container may exist on a server, but Atlas production does not depend on it.

Historical rollout evidence on 2026-03-28 included tags 50c4169 and f21ec3d. Nginx restart caused a short readiness gap, handled by the deploy script's smoke retries. Browser cache may hide frontend changes during manual checks. These observations explain current tooling; they are not a report of the current deployment.
