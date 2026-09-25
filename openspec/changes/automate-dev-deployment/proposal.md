# Automate development deployment

## Problem

After a change is integrated into `develop`, publishing its ARM64 images and activating them at `dev.humami.es` requires manual SSH, GHCR authentication, image pinning, health checks and deployment-record updates. This is slow, repeatedly consumes agent context and leaves a larger operational error surface.

## Goal

Deploy each successfully built `develop` revision to the isolated DEV runtime automatically, with immutable image digests, bounded health verification and an auditable deployment record.

## Scope

- Extend the existing development-image workflow with a post-build deploy job.
- Authenticate to the server with a dedicated deploy SSH key stored as a GitHub secret.
- Use a dedicated GHCR read-only token from a GitHub secret only while pulling images on the server; remove the Docker login afterwards.
- Pin the retrieved ARM64 image digests, restart only DEV application containers, wait for health, and record the verified revision.
- Document one-time secret and server preparation plus rollback.

## Non-goals

- Production deployment, production credentials, billing budgets, or a self-hosted runner.
- Automatic publication of guides, PDFs or other editorial content.

## Success metric

A merged `develop` revision reaches DEV without interactive SSH or GitHub Mobile approval, and the workflow reports the deployed immutable revision or fails with actionable health output.
