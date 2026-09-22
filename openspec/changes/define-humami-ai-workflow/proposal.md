## Why

Humami needs a consistent AI workflow across product definition, development, code delivery, editorial work and production operations. Existing instructions are scattered, some references are missing, and the documented release policy conflicts with the operational runbook. Loading all procedures into every conversation would add irrelevant context.

This change defines a small entry point, selectively loaded skills and temporary specialist agents. Hugo reviewed the definition and explicitly requested implementation on 2026-09-22. Future definition-only requests still do not authorize implementation.

## What Changes

- Add a short project entry point that routes requests to relevant procedures.
- Define temporary specialist profiles and explicit delegation responsibilities under a coordinating task agent.
- Add reusable skills for OpenSpec feature definition, development, code review, code delivery, meal preparation, blog drafting, content publication and releases.
- Reuse existing engineering rules and runbooks as selectively loaded references; migrate the existing meal skill without duplicate copies.
- Define normal implementation delivery as integration into develop through a PR, independent AI review and successful required checks.
- Align the target release policy so production originates exclusively from master, using an identified version and an explicit deployment request.
- Keep draft creation, Git delivery, content publication and production deployment as distinct operations.

## Capabilities

### New Capabilities

- `selective-agent-context`: minimal initial instructions and task-specific procedure discovery.
- `specialist-coordination`: temporary agents with explicit ownership and independently reviewed results.
- `code-delivery-workflow`: specification-first Git Flow and evidence-based integration.
- `content-release-workflows`: preparation and publication procedures for content and production releases.

### Modified Capabilities

None. The repository has no existing OpenSpec capability specifications; the capabilities above describe the target operating workflow, including changes to existing Markdown policies.

## Impact

- Future implementation affects agent instructions, skills, workflow documentation and reference links. It does not redesign Next.js, Spring Boot, MongoDB or S3.
- Existing `specs/` remain available. Spec 016 links here as a compatibility index; OpenSpec artifacts are the canonical definition for this change.
- CI and GitHub protection enforcement are prerequisites for autonomous integration and will be delivered in a separate linked change. This definition does not enable automatic merging or alter remote settings.
- LinkedIn, business features, API changes, validator fixes, new publishing clients, real content writes, actual releases, bulk merging develop into master, new MCP servers and permanent orchestrator services are outside this change.
- Known runtime/tooling gaps are recorded in design.md as follow-up dependencies rather than silently expanding this task.
