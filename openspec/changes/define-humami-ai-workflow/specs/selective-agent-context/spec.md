## Purpose

Provide Humami agents with the minimum relevant instructions for the requested task while retaining shared, versioned project knowledge.

## ADDED Requirements

### Requirement: Minimal initial context
The workflow SHALL expose a brief project entry point and SHALL load detailed procedures only when the current task requires them.

#### Scenario: Draft a blog article
- **WHEN** the user requests a blog draft
- **THEN** the agent selects editorial instructions and relevant brand references without loading backend development or production deployment procedures

### Requirement: Discoverable and maintained procedures
The workflow SHALL make project skills discoverable and SHALL resolve their references to maintained resources without requiring private conversation memory.

#### Scenario: Prepare a meal from an external source
- **WHEN** the meal preparation skill is selected in a fresh project session
- **THEN** its templates, contracts and validation references are available from the repository and there is no dependency on another user's private workspace

### Requirement: Planning does not start implementation
The workflow SHALL distinguish defining a change from applying it and SHALL keep implementation pending until requested.

#### Scenario: Save an OpenSpec definition
- **WHEN** the user requests only an OpenSpec description
- **THEN** the agent saves and validates the change artifacts, leaves implementation tasks unchecked and does not launch another implementing task
