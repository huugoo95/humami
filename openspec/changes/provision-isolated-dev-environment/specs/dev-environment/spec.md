## ADDED Requirements

### Requirement: Isolated development data
The dev environment SHALL use independent database storage, credentials and write secret. It MUST NOT use production write credentials or copy production data implicitly.

#### Scenario: Empty isolated database
- **WHEN** dev starts for the first time
- **THEN** its recipe API SHALL return its own empty catalogue and no production records

#### Scenario: Missing image storage
- **WHEN** a dev image upload is requested before separate storage is configured
- **THEN** it SHALL fail explicitly without contacting production S3 with real credentials

### Requirement: Safe shared hosting
Dev SHALL have memory/CPU/log limits, no public database/application ports and HTTPS routing at dev.humami.es. Production containers and upstream configuration MUST remain unchanged except the proxy addition needed for dev.

#### Scenario: Activation
- **WHEN** dev is activated
- **THEN** dev home/catalogue/API SHALL respond over valid TLS and production home/catalogue/API SHALL remain healthy on unchanged application image IDs

### Requirement: Explicit cleanup and provenance
Cleanup SHALL remove only the approved obsolete images and retain all existing volumes. Dev images MUST identify a verified develop commit and match ARM64 architecture.

#### Scenario: Resource cleanup
- **WHEN** cleanup completes
- **THEN** active and retained rollback images SHALL remain available and freed disk SHALL be measured
