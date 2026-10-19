---
name: humami-feature-spec
description: Define Humami product features in OpenSpec before implementation. Fixes, refactors, dependency, CI/ops and docs changes do not need a spec.
---

# Define a Humami change

1. Inspect the affected code and existing `openspec/changes/` and historical `specs/`. For product decisions consult [priorities](../../../docs/HUMAMI.md); read brand or architecture only if affected.
2. Preserve the project's required definition content within the OpenSpec artifacts: problem and user impact; goal and success metric; priority; user story; scope/non-goals; observable Given/When/Then acceptance criteria; edge cases; risks and dependencies. Design affected frontend/backend responsibilities, API/data contracts and any migration/security concerns. Mark genuinely inapplicable fields with a reason instead of inventing metrics or risks. Resolve material ambiguity before writing implementation tasks.
3. Use `spec-driven`: `proposal.md`, `design.md`, `specs/<capability>/spec.md`, `tasks.md` and `.openspec.yaml` under `openspec/changes/<change-id>/`. Reference any existing baseline capabilities under `openspec/specs/` before deciding ADDED versus MODIFIED requirements. Each requirement uses SHALL/MUST and has a `#### Scenario:` with WHEN/THEN. Keep implementation tasks unchecked.
4. Keep technical definitions in English. Allocate the next unused numeric spec ID for product features and add a thin link in `specs/`; requirements remain canonical in OpenSpec. Use the same ID for branch and PR. Keep the spec proportional: a small feature needs a short proposal and tasks, not every artifact.
5. If file changes are needed, use a branch from `master` per [code delivery](../humami-code-delivery/SKILL.md). Do not turn a definition-only request into implementation.
6. Run `openspec validate <change-id> --strict` from the repo root if the CLI is available; if unavailable, report that limitation or use the official CLI through an authorized temporary installation. Check reference paths and proposal-to-capability consistency.

Deliver links to the definition and actual validation evidence. Artifact completion is not approval to apply. Implement only on a separate request. Archive only after implementation and acceptance; do not copy proposed specs into the current baseline prematurely.
