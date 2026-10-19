# production-image-deployment

## ADDED Requirements

### Requirement: Deployment reclaims disk space safely before pulling

The production deployment SHALL remove Docker images and build cache that no container uses before pulling new images, SHALL preserve images of existing containers and all volumes, and SHALL fail before pulling when free space stays below the configured minimum.

#### Scenario: Old release images accumulated

- **WHEN** previous release images that no container uses fill the host disk
- **THEN** the deployment removes them, logs disk usage, and pulls the new release

#### Scenario: Space still insufficient

- **WHEN** free space after pruning is below `MIN_FREE_MB`
- **THEN** the deployment fails before pulling or recreating containers and prints disk diagnostics
