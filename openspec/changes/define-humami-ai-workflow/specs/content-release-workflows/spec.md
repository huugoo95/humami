## Purpose

Provide explicit, verifiable workflows for Humami content preparation, authorized publication and versioned production releases.

## ADDED Requirements

### Requirement: Content preparation matches current contracts
The workflow SHALL prepare content compatible with current API and rendering behavior, record its provenance and check documented content rules before publication.

#### Scenario: Recipe has only one step
- **WHEN** an imported preparation contains only one instruction step
- **THEN** preparation is reported as failing the documented minimum and is not passed to publication as valid even if the current validator accepts it

#### Scenario: Blog draft
- **WHEN** a blog article is prepared
- **THEN** its body is compatible with the current plain-text renderer and no LinkedIn publication workflow is activated

### Requirement: Authorized and traceable publication
The workflow SHALL separate drafting from real writes and SHALL verify authorization, authentication, target and publication result without exposing secrets.

#### Scenario: Image fails after meal creation
- **WHEN** creation returned an ID but image upload failed
- **THEN** the agent records the partial result and reuses that ID for an authorized retry rather than creating another meal

#### Scenario: Creation outcome is uncertain
- **WHEN** creation times out before an ID is received
- **THEN** the agent reconciles the outcome or reports uncertainty instead of blindly repeating the creation request

### Requirement: Production originates from master
The release workflow SHALL deploy production only from a concrete version originating from master, following an explicit deployment request and identifying the previous known-good version.

#### Scenario: Develop-only candidate
- **WHEN** the requested production candidate is not incorporated into master
- **THEN** the agent reports the required release integration and does not deploy that develop-only candidate

#### Scenario: Versioned deployment
- **WHEN** an authorized production release is executed
- **THEN** the agent records the master commit and corresponding image version, verifies the deployment and uses the documented recovery procedure if needed
