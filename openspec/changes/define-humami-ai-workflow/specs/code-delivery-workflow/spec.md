## Purpose

Make Humami code delivery traceable from an agreed specification to a reviewed and validated integration through the project's Git Flow.

## ADDED Requirements

### Requirement: Specification-first branches
The workflow SHALL associate product/code work with a written specification and SHALL use an appropriate working branch from the documented base before implementation.

#### Scenario: Implement a normal feature
- **WHEN** the user requests implementation of a defined feature
- **THEN** the agent uses a feature branch based on develop, follows the project's naming convention and preserves existing user changes

### Requirement: Evidence-based develop integration
The normal implementation workflow SHALL target integration into develop through a PR with independent AI review and successful required checks, subject to effective permissions and branch protections.

#### Scenario: Checks fail
- **WHEN** a required check fails for the proposed integration
- **THEN** the agent does not merge and reports or fixes the failing condition within the task's scope

#### Scenario: Enforcement is unavailable
- **WHEN** required checks are not configured or branch protections require an approval the agent cannot provide
- **THEN** the agent leaves integration pending and identifies the prerequisite without bypassing it or claiming completion

### Requirement: Separate external actions
The workflow SHALL distinguish local preparation, pushing a branch, integrating code and deploying production according to the user's requested scope.

#### Scenario: Definition-only task
- **WHEN** the user asks to prepare the task description before implementation
- **THEN** saving the definition does not trigger a push, merge, implementation or deployment
