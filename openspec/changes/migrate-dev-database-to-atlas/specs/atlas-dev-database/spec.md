## ADDED Requirements

### Requirement: Dedicated Atlas development persistence

The dev runtime SHALL use `humami_dev` in the existing Atlas cluster through a dedicated database user that has access only to `humami_dev`. It MUST NOT persist or use the production application database credential at runtime.

#### Scenario: Dev backend starts

- **WHEN** the dev backend starts after migration
- **THEN** it SHALL connect to the dedicated Atlas URI and database `humami_dev`, without requiring the host-local MongoDB service

#### Scenario: Isolation

- **WHEN** dev performs an application write
- **THEN** that write SHALL target only `humami_dev` and shall not authenticate with the production application user

### Requirement: Verified and recoverable migration

The migration SHALL preserve collection counts and index definitions from local `humami_dev` before switching the backend. It MUST retain a local backup and volume until explicit cleanup.

#### Scenario: Migration validation

- **WHEN** the local dev data has been restored to Atlas
- **THEN** the recorded collection inventory on Atlas SHALL match the local source before the backend is switched

#### Scenario: Migration failure

- **WHEN** Atlas restore or verification fails
- **THEN** the dev backend SHALL be restored to the local data store and the local volume SHALL remain intact

### Requirement: Shared-host safety after Atlas migration

The development runtime SHALL stop its host-local MongoDB service only after dev and production verification succeeds. Production application containers, data and image identities MUST remain unchanged.

#### Scenario: Successful cutover

- **WHEN** the dev backend is configured for Atlas
- **THEN** dev home, catalogue and API SHALL respond over HTTPS and production home, catalogue and API SHALL remain healthy
