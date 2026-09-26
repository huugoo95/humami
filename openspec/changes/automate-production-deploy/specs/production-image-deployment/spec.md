# production-image-deployment

## ADDED Requirements

### Requirement: Immutable master image publication

The system SHALL build Linux ARM64 backend and frontend production images for
each qualifying `master` commit and SHALL publish them to GHCR under that
commit's full SHA using the GitHub Actions token with package-write permission.
It MUST NOT publish or deploy a `latest` tag.

#### Scenario: Master release image publication

- **WHEN** a commit is pushed to `master`
- **THEN** the workflow publishes `humami-backend:<full-sha>` and
  `humami-frontend:<full-sha>` after both ARM64 builds succeed

#### Scenario: Master pull-request validation

- **WHEN** a pull request targets `master`
- **THEN** both ARM64 images are built for validation without publishing an
  image or connecting to production

### Requirement: Exact-version production deployment

The system SHALL deploy production only after successful publication of both
images for the same full master SHA. It MUST pass that exact SHA as `IMAGE_TAG`
to the production Compose deployment and MUST serialize concurrent production
runs without cancelling a run already updating production.

#### Scenario: Successful master deployment

- **WHEN** both images for a master SHA are published successfully
- **THEN** the deployment uses that SHA for both services and executes the
  production smoke checks

#### Scenario: Failed image publication

- **WHEN** either image build or publication fails
- **THEN** the production deployment job SHALL NOT run

### Requirement: Least-privilege registry access

The system SHALL use the per-run GitHub Actions token to publish images and a
repository secret restricted to package read access only while the production
server pulls them. It MUST remove the server's registry session after the
attempt and MUST NOT write either credential to repository files or logs.

#### Scenario: Registry authentication cleanup

- **WHEN** a production deployment completes or fails after remote registry
  authentication
- **THEN** the remote GHCR session is removed before the job exits

### Requirement: Traceable release evidence

The workflow SHALL make the source revision and exact deployed image tags
observable in the job output. A workflow failure SHALL distinguish publication,
remote deployment and smoke verification failures.

#### Scenario: Release investigation

- **WHEN** an operator opens a completed production workflow run
- **THEN** they can identify the full master SHA and the exact two image tags
  associated with the run without retrieving a credential
