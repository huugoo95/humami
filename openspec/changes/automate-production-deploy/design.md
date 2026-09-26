# Design

## Workflow topology

`production-deploy.yml` has two phases:

1. A pull request to `master` builds both ARM64 images without pushing or
   connecting to production. This validates the build artifact that will be
   released after merge.
2. A push to `master` builds and publishes both images under the full
   `github.sha`, then deploys that same SHA to production. The deploy job
   depends on both image builds and is
   serialized through one production concurrency group. In-progress releases
   are never cancelled because doing so could interrupt a live update.

The workflow applies path filters to master push and pull-request triggers.
It does not accept arbitrary SHA dispatches: recovery remains the explicit,
versioned operation owned by the release procedure.

## Image contract

The backend and frontend image names remain
`ghcr.io/<owner>/humami-backend:<sha>` and
`ghcr.io/<owner>/humami-frontend:<sha>`. The full commit SHA is the only
production tag used by this workflow. The build context follows the established
DEV workflow: Maven wrapper files are copied into the backend context before
Docker builds, and the frontend receives its production `/api` base URL at
build time. OCI source and revision labels make images traceable.

## Deployment contract and secrets

The deploy job writes the SSH private key and known-hosts material to a private
temporary directory, uses it with explicit host-key and identity options, then
copies the versioned Compose, smoke and deployment scripts into the runtime
directory. It streams `PROD_GHCR_READ_TOKEN` to `docker login` on the server.
The remote shell uses that session only to run:

`IMAGE_TAG=<sha> GHCR_OWNER=<owner> ./scripts/deploy-images.sh`

A shell trap logs out of GHCR even when the deployment command fails. The token
is never included in an image tag, workflow log, repository file or remote
environment file. `deploy-images.sh` already pulls exact images, recreates
services and runs `smoke-prod.sh` against `https://humami.es`.

Required repository secrets are:

- `PROD_DEPLOY_HOST`
- `PROD_DEPLOY_USER`
- `PROD_DEPLOY_SSH_KEY`
- `PROD_DEPLOY_KNOWN_HOSTS`
- `PROD_GHCR_READ_TOKEN` (classic PAT restricted to `read:packages`)

GitHub Actions uses its per-run `GITHUB_TOKEN` with `packages: write` only for
publishing. No personal write token is part of the release path.

## Failure, verification and rollback

A failed image build prevents deployment. A failed pull, Compose update or
smoke check fails the deploy job and preserves the server-side tool output for
investigation; it does not claim success. After an automatic deployment, the
workflow reports the full source SHA and exact image tags.

To roll back, perform the documented explicit versioned recovery through the
release procedure with a previously verified master version. No database
rollback is implied.
