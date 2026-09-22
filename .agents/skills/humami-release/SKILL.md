---
name: humami-release
description: Prepare a Humami Git Flow release or execute an explicitly requested production deployment from master with concrete image versions. Excludes normal develop integration and content publication.
---

# Release Humami

## Select the requested phase
A release-preparation request permits planning/branch/PR work, not deployment. Normal feature delivery ends in develop. For Git operations use [code delivery](../humami-code-delivery/SKILL.md); for topology consult [deployment architecture](../../../docs/ghcr-deploy.md) only when needed.

1. Prepare `release/<version>` from develop (or `hotfix/<name>` from master) and verify scope, checks and review. Integrate to master via PR and back-merge to develop. Do not bulk-promote unrelated accumulated changes without an agreed release scope.
2. For production require an explicit deployment request, exact destination and an identified commit incorporated into fetched origin/master. Verify ancestry; a develop-only commit is not deployable. Unknown/stale remote state is a blocker, not proof.
3. Check that no active incident blocks the release and that the approved host has working Docker/Compose, registry access, runtime Compose/nginx files, required environment values and mounted TLS certificates/keys. Verify existing TLS/renewal health without changing it or displaying secrets. If prerequisites are missing, stop and report them; do not substitute the local MongoDB profile for Atlas production. Record the currently running known-good image identifiers and any data compatibility constraints before changes. Do not infer deployed state from Git or assume staging exists.
4. Use a clean checkout of the verified candidate to build images. Set `IMAGE_TAG` to its commit-derived tag and `PUBLISH_LATEST=false`, and supply registry credentials externally when invoking `scripts/release-build-push.sh`. Confirm runtime architecture compatibility: the current script defines PLATFORM but does not pass it to docker build. If the builder/host differ, resolve that gap before building rather than assuming cross-compilation.
5. Verify the actual image digests/provenance for both images. On the authorized destination set the exact `IMAGE_TAG` and GHCR owner before `scripts/deploy-images.sh`. Never accept its implicit latest default as a production version.
6. Verify with `scripts/smoke-prod.sh <approved-base-url>`, actual running image identifiers and relevant user behavior. The current deploy script hardcodes humami.es for smoke checks; use it only for that destination. Record candidate, images, checks, time and outcome.

If verification fails, report the failure and use [manual recovery](references/recovery.md) within the release's authorized recovery scope. Do not claim automated rollback or reverse data migrations blindly. Production credentials, certificates and private keys stay outside Git.
