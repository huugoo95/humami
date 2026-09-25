# Design

The existing `dev-images.yml` remains the single workflow for ARM64 image publication. A `deploy-dev` job runs only for a successful push to `develop`, after both image matrix entries have published their immutable `dev-<SHA>` tags.

The job uses GitHub-hosted infrastructure. It receives `DEV_DEPLOY_HOST`, `DEV_DEPLOY_USER`, `DEV_DEPLOY_SSH_KEY` and `DEV_GHCR_READ_TOKEN` as Actions secrets. The private GHCR token is streamed over SSH stdin to the remote script and is never written to the workflow output, Compose files or deployment record. The remote script logs into GHCR, pulls exact tags, resolves `RepoDigests`, and logs out with a trap even on failure.

The remote script writes `/home/ubuntu/apps/humami-dev/docker-compose.dev.pinned.yml` atomically with digest-only references. It validates the combined Compose configuration, recreates only `dev-backend` and `dev-frontend`, and polls `https://dev.humami.es/`, `/meals` and `/api/guides`. The backend startup allowance is intentionally longer than normal because Atlas discovery can take several minutes on the constrained host. On success it writes a secret-free `deployment.json` with source SHA, image digests, timestamp and HTTP results.

The production Compose project is never addressed. The DEV Compose project retains its database container and volumes. Failed validation or health checks leave diagnostics in the Actions log. The script snapshots the prior Compose definition and pins for its own execution, then restores them for a health or activation failure; operators may also re-run a previous SHA through workflow dispatch. Manual dispatch checks out the requested SHA before uploading its Compose definition.
