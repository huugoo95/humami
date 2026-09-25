# DEV deployment automation

## ADDED Requirements

### Requirement: Deploy after immutable DEV image publication

The development image workflow SHALL deploy only after all ARM64 images for a `develop` push are published successfully, and SHALL use the commit SHA as the deployment identity.

#### Scenario: Successful develop integration

- **WHEN** the image jobs for a `develop` commit succeed
- **THEN** the deployment job pulls the corresponding immutable backend and frontend images and deploys them to DEV

### Requirement: Secret-safe remote registry access

The deployment SHALL use a dedicated read-only GHCR credential and SHALL remove remote Docker registry authentication after image retrieval. It MUST NOT write credentials to source control, Compose files or deployment records.

#### Scenario: Image pull completes

- **WHEN** both DEV images have been retrieved
- **THEN** the server Docker login is removed before application activation continues

### Requirement: Isolated digest-pinned activation

The deployment SHALL write digest references for the retrieved images, validate the DEV Compose configuration and recreate only the DEV frontend and backend containers.

#### Scenario: Activation

- **WHEN** image digests are resolved
- **THEN** production containers and DEV database volumes remain untouched

### Requirement: Verified deployment evidence

The deployment SHALL wait for bounded HTTP health checks of DEV home, meals and guides endpoints. On success it SHALL record the source SHA, digests, timestamp and results in a secret-free record.

#### Scenario: Endpoint does not become healthy

- **WHEN** a required endpoint does not return HTTP 200 before the timeout
- **THEN** the workflow fails with container diagnostics and does not mark the deployment verified
