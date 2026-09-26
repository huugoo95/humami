# Spec 026 — Automate production image deployment

## Why

A production release currently requires a locally created personal write token,
manual image publication, temporary server authentication and a manual Compose
update. This adds delay and credential handling to every approved master
release, even though the development image workflow already builds ARM64 images
from a protected branch with GitHub Actions.

## Goal and priority

Priority: high operational reliability. As the owner, I want an approved merge
to `master` to build, publish and deploy the exact ARM64 images to production
so that releases are reproducible without a personal publishing token.

Success metric: a master commit results in two GHCR images tagged with its full
SHA, the production server runs those exact tags, and public smoke checks pass.

## Scope

- Add a GitHub Actions production workflow for master pull-request validation,
  master image publication and production deployment.
- Build backend and frontend images for Linux ARM64, tag them with the exact
  Git commit SHA and publish through the workflow `GITHUB_TOKEN`.
- Deploy only the exact SHA passed by the workflow, synchronize the versioned
  Compose and smoke/deployment scripts, authenticate the server temporarily
  with a read-only GHCR token, then log it out.
- Serialize production workflow executions and run the existing public smoke
  checks after the Compose update.
- Document required GitHub repository secrets and the rollback behavior.

## Non-goals

- No change to application code, database schema, frontend behavior, DNS,
  TLS, Nginx configuration, development deployment or package visibility.
- No default/latest production image tag, automatic rollback or deletion of
  existing images.
- No persistent write-capable credential on the server.

## User story

As the owner, when a reviewed release is merged into `master`, production is
updated to that immutable commit version and reports a verified result without
requiring a new personal publishing token.

## Risks and dependencies

The workflow depends on GitHub Actions retaining write access to both private
GHCR packages, and on four repository secrets: `PROD_DEPLOY_HOST`,
`PROD_DEPLOY_USER`, `PROD_DEPLOY_SSH_KEY`, `PROD_DEPLOY_KNOWN_HOSTS`, plus the
read-only `PROD_GHCR_READ_TOKEN`. The server must retain its current
`/home/ubuntu/apps/humami` runtime and `scripts/deploy-images.sh` contract.

A bad master merge can still deploy a bad release. Images remain tagged by
commit so rollback is an explicit rerun/deployment of the known-good SHA;
image cleanup is a separate operation. Recovery is an explicit versioned
operation through the release procedure, not a dispatch of an arbitrary SHA.

## Delivery

Workflow and operational documentation changes require validation, independent
review and a PR to `develop`. They do not themselves deploy production; the
workflow becomes active only after its master release is merged.
