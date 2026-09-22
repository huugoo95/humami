## Context

See proposal.md for motivation and capability specifications for observable requirements.

The inspected develop checkout has engineering rules, Git Flow documentation, ops runbooks, a meal authoring skill and release scripts, but no root AGENTS.md or OpenSpec configuration. It has no committed GitHub Actions workflows. The existing ops decision names Hugo as reviewer, while the agreed target is independent AI review plus required checks. Production runbooks currently mention develop, while the agreed target is master only.

The blog renders content as plain text. Meals are created before their images are uploaded. Write endpoints use X-HUMAMI-SECRET. The meal validator does not enforce the playbook's minimum of two steps. These are concrete constraints on what workflow instructions can claim.

## Goals / Non-Goals

Goals: establish discoverable, selectively loaded operating procedures and unambiguous responsibility boundaries, using repository knowledge as durable context. Keep technical documents in English and user-facing culinary content in Spanish.

Non-goals: implement product functionality, start background agents, publish real content, deploy, create a custom MCP server, install external connectors, implement CI, change GitHub protections, or repair application/tooling defects as part of this change. LinkedIn is excluded.

Current authorization is limited to authoring and validating this definition. All implementation tasks remain unchecked until implementation is separately requested.

## Decisions

### 1. Progressive disclosure

Keep the future root AGENTS.md to approximately 30–40 lines: repository map, preservation of local work, branch checks, actual-versus-planned state and procedure selection. Do not embed Git Flow, TDD, content contracts or deployment runbooks there. Do not add directory-specific AGENTS.md files in this first version.

Store project skills in `.agents/skills/`. Skill descriptions expose their purpose; bodies load when selected; detailed references load only when relevant. Resolve all reference paths relative to their actual file location and verify discovery from the repository in the target Codex environment. Avoid mandatory personal installation and duplicated global copies.

The alternative of one comprehensive AGENTS.md was rejected because it loads unrelated instructions into every task. A custom orchestration framework is unnecessary for this repository.

Migrate operational procedures (sequence, validation and stopping conditions) into the owning skill instead of merely linking to every old document. Keep shared knowledge such as architecture, brand and API contracts in maintained reference documents, and keep executable logic in scripts. Remove migrated duplicate instructions or replace them with a pointer to their canonical location. A skill links conditional references without requiring all of them to be read on every invocation.

### 2. Temporary agents, not persistent services

The primary task agent is the coordinator. Store specialist instructions in `ops/agents/` for product, frontend, backend, editorial, operations and reviewer, with a short routing index read only for delegation. These files do not instantiate sessions by themselves.

Small work stays with the coordinator. Complex work may delegate independently executable parts after identifying dependencies. Code delivery requires an independent reviewer. A delegation includes objective, scope/ownership, references, expected evidence, permitted external actions and return criteria. Frontend/backend work agrees on an API contract before parallel execution. Concurrent implementations use explicitly created branches/worktrees; read-only review need not create another checkout.

### 3. Skill boundaries

| Skill | Responsibility | References read when needed |
| --- | --- | --- |
| humami-feature-spec | Produce proposal, requirements, design and tasks using OpenSpec; no implicit implementation | Strategy and affected code; historical specs for context |
| humami-development | Implement the agreed change with relevant tests | ENGINEERING_RULES.md, architecture and area conventions |
| humami-code-review | Independently inspect acceptance criteria, regressions and evidence | Change artifacts, diff, relevant code and checks |
| humami-code-delivery | Branch, commit, push, PR and integrate to the requested destination | Git Flow, PR template and effective protection requirements |
| humami-meal-authoring | Normalize sources into a reviewed MealRequest; no writes | Meal model, templates, validator and culinary playbook |
| humami-editorial | Draft Spanish blog content compatible with the current renderer | BRAND.md, CONTENT.md and blog request model |
| humami-content-publish | Publish reviewed meals, blog or about data when authorized | Only the selected endpoint's contract, authentication and verification steps |
| humami-release | Prepare or execute the explicitly requested release phase | Git Flow, GHCR and deployment runbooks, existing scripts |

Migrate the existing skill from `skills/humami-meal-authoring/`; update its callers and remove the obsolete copy only in the implementation phase. Remove dependencies on missing recipe-ingestion-pipeline documentation and private workspace memory by pointing to maintained repository references. Reuse docs/ for knowledge, ops/ for operational procedures and scripts/ for executable tools. Create content/ drafts only when there is an actual draft, not placeholder directories.

### 4. OpenSpec transition and Git delivery

New feature definitions use OpenSpec. Preserve historical specs/ files; retain the numeric spec ID in a thin compatibility entry for this change so existing branch/PR conventions remain traceable. Do not duplicate requirements there. Future delivery references the OpenSpec change and any compatibility ID required by the project.

Normal functionality branches follow `feat/<spec-id>-<short-name>` from updated develop; fixes, documentation, hotfixes and releases follow their documented branch types. No implementation directly on develop/master. A definition-only request stops after its artifacts are written and validated; it does not launch another task, invoke apply, push or merge implicitly.

For implementation requests, the target completion is merge into develop after a PR, independent review and required green checks on the candidate commit. New edits invalidate affected review/check evidence. The delivery skill verifies origin and base branch before pushing to avoid using a branch's inherited upstream incorrectly.

The human-only review policy will be replaced in documentation with the agreed AI-plus-checks target. Actual checks/protections remain authoritative: if missing or incompatible, leave the PR pending and report the specific prerequisite. Do not pretend agent commentary is a GitHub approval or bypass protections.

### 5. Content and release boundaries

Preparation produces a local draft/payload and source references. Publishing consumes that reviewed artifact, the target environment and authorization. Preserve existing content review requirements; do not infer permission from skill discovery or duplicate already supplied approval.

The publication procedure uses secret injection without printing credentials. Track source, payload, created ID and verification result. If image upload fails after meal creation, resume using the saved ID. If creation times out before an ID is known, reconcile or report the uncertain result instead of blindly creating again. Do not claim server-side idempotency.

The meal preparation skill explicitly checks the two-step minimum until the validator gap is fixed in a separate change. Blog content must match plain-text rendering; do not promise Markdown or HTML rendering. LinkedIn is not described as an available workflow.

Releases follow develop to release branch to master, then deploy a version originating from master only when requested. The skill requires a concrete image tag tied to the commit and records the previous known-good version. Existing script defaults such as latest do not replace that requirement. Follow the existing manual recovery runbook; do not claim automatic rollback.

### 6. Follow-up dependencies

- CI and protection enforcement: a separate change must establish required checks and the remote configuration before autonomous integration is enabled. This change only documents the prerequisite.
- Recipe validator: a separate behavior change should enforce two steps and relevant malformed-input cases in executable validation.
- Publishing reliability: any new retry/idempotency client or API changes need a separate spec. This change documents safe use of the existing API.
- Production state and staging availability: inspect when an actual release is requested. Never infer deployed state from branch names or assume a staging environment exists.

## Risks / Trade-offs

- Instruction compliance is not enforcement: validate routing behavior and use real CI/protections when available.
- Legacy docs may contradict new skills: reconcile active references, preserving historical specs as history rather than rewriting them as implemented policy.
- Too many entry points may overlap: keep descriptions discriminating; development delegates delivery rather than duplicating its procedure.
- Independent reviewers may still miss bugs: require executable evidence and do not claim review proves correctness.

## Migration Plan

After this definition is reviewed and implementation is requested, implement the instructions/skills, migrate the existing meal skill, update active references and validate representative scenarios. Do not configure remote systems or operate production as part of rollout. Rollback is a Git revert of the instruction changes through the normal PR flow. Archive this OpenSpec change only after implementation is complete and accepted; until then do not publish proposed requirements as current baseline specs.
