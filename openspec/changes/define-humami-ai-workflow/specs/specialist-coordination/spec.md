## Purpose

Coordinate bounded specialist work within a Humami task while retaining clear responsibility for integration and final verification.

## ADDED Requirements

### Requirement: Scoped specialist selection
The coordinator SHALL select specialists based on the affected work and dependencies, and SHALL provide scope, references, expected results and permitted actions with each delegation.

#### Scenario: Cross-stack functionality
- **WHEN** a feature requires parallel frontend and backend changes
- **THEN** the coordinator establishes the shared contract and assigns bounded work to each specialist before implementation proceeds in parallel

### Requirement: Explicit editing isolation
The workflow SHALL establish explicit ownership and isolated worktrees for independent concurrent implementations rather than assuming subagent sessions isolate files.

#### Scenario: Two implementations run concurrently
- **WHEN** two specialists must edit independently
- **THEN** their branches and working directories are explicitly assigned and the coordinator remains responsible for integrating and testing their combined result

### Requirement: Independent review
The workflow SHALL obtain an independent review before code integration and SHALL associate findings with the reviewed version.

#### Scenario: Changes after review
- **WHEN** implementation changes after the reviewer assessed a candidate
- **THEN** affected review and validation evidence is refreshed before that candidate can be considered ready for integration
