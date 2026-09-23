## ADDED Requirements

### Requirement: Evidence-based remediation
The change SHALL identify actual dependency versions and assess critical/high advisories with dated sources, patched versions and applicability. Remaining risks MUST be explicitly reported, not presented as a clean audit.

#### Scenario: Advisory assessment
- **GIVEN** a vulnerable dependency appears in the resolved graph
- **WHEN** remediation is selected
- **THEN** the record SHALL distinguish runtime/tooling exposure and either confirm a fixed resolved version or document the unresolved risk and blocker

### Requirement: Reproducible compatible frontend update
The frontend SHALL use patched Next15.5 dependencies with matching lint configuration and Node24 LTS. Committed lockfile installation MUST be reproducible.

#### Scenario: Fresh frontend validation
- **WHEN** CI installs from the updated lockfile on Node24
- **THEN** audit SHALL report no high/critical npm findings and unit tests, lint and production build SHALL pass

### Requirement: Backend regression safety
The backend SHALL remediate identified critical dependency findings while preserving API contracts and the existing full test suite.

#### Scenario: Full backend validation
- **WHEN** Maven verify runs with the updated dependency graph and isolated MongoDB
- **THEN** all tests including pagination integration tests SHALL pass and the executable artifact SHALL be produced

### Requirement: Gated delivery
The implementation SHALL receive independent review and successful remote frontend/backend checks before integration into develop. No production deployment is implied.

#### Scenario: Delivery
- **WHEN** checks or review fail
- **THEN** the candidate SHALL remain unmerged until corrected or the blocker is explicitly resolved
