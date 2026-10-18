# production-image-deployment

## ADDED Requirements

### Requirement: Deployment executes the deployed revision's scripts

The production deployment SHALL execute the deployment and smoke scripts from the commit being deployed, and SHALL fail before recreating containers if the scripts on the host differ from that commit.

#### Scenario: Scripts updated in a release

- **WHEN** a release changes `scripts/smoke-prod.sh` or `scripts/deploy-images.sh`
- **THEN** the production job copies them to the path it executes and verifies their checksums before deploying

#### Scenario: Slow backend start

- **WHEN** the backend needs longer than 90 seconds but less than the configured readiness window to serve `/api/meals`
- **THEN** the smoke check waits and the deployment succeeds
