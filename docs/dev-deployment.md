# Automated DEV deployment

A push to `develop` that builds development images runs the `Deploy DEV` job after both ARM64 image publications succeed. The job deploys only `/home/ubuntu/apps/humami-dev`; production Compose services are never addressed.

## Required GitHub Actions secrets

- `DEV_DEPLOY_HOST`: DEV server host.
- `DEV_DEPLOY_USER`: SSH deployment user.
- `DEV_DEPLOY_SSH_KEY`: dedicated private key authorized only for that deployment user.
- `DEV_DEPLOY_KNOWN_HOSTS`: pinned SSH host key line for the DEV server.
- `DEV_GHCR_READ_TOKEN`: dedicated classic GitHub token with only `read:packages`; it is streamed over SSH only to pull images and the remote Docker login is removed immediately afterwards.

The server stores immutable image references in `docker-compose.dev.pinned.yml` and a secret-free result in `deployment.json`.

## Failure and rollback

The job waits up to six minutes for `https://dev.humami.es/`, `/meals` and `/api/guides`. It prints DEV backend diagnostics on timeout and never alters production. To roll back deliberately, dispatch the workflow with a previously verified `develop` SHA. During an execution, it keeps a private temporary copy of the active Compose definition and image pins; a health or activation failure restores those files and recreates only the two DEV application containers.

Rotate the SSH key and package token periodically or immediately after suspected exposure.
