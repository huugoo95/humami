# frontend-ci

## ADDED Requirements

### Requirement: Dependency audit gate with expiring exceptions

Frontend CI SHALL fail on any high or critical npm advisory unless the advisory has an unexpired, justified entry in `humami-web/audit-allowlist.json`. It SHALL also fail on any high or critical advisory in production dependencies, regardless of the allowlist.

#### Scenario: Unlisted advisory

- **WHEN** `npm audit` reports a high or critical advisory with no allowlist entry
- **THEN** the audit gate fails and names the advisory and affected packages

#### Scenario: Expired exception

- **WHEN** an allowlist entry's `expires` date has passed
- **THEN** the audit gate fails until the entry is renewed with justification or the advisory is resolved

#### Scenario: Production dependency advisory

- **WHEN** a production dependency has a high or critical advisory
- **THEN** CI fails even if the advisory is allowlisted
