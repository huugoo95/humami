---
name: humami-feature-spec
description: Define Humami changes in OpenSpec before implementation. Use for feature scoping or task descriptions, not for executing an already agreed change.
---

# Define a Humami change

1. Inspect the affected code and existing `openspec/changes/` and historical `specs/`. For product decisions consult [priorities](../../../docs/HUMAMI.md); read brand or architecture only if affected.
2. Preserve the project's required definition content within the OpenSpec artifacts: problem and user impact; goal and success metric; priority; user story; scope/non-goals; observable Given/When/Then acceptance criteria; edge cases; risks and dependencies. Design affected frontend/backend responsibilities, API/data contracts and any migration/security concerns. Mark genuinely inapplicable fields with a reason instead of inventing metrics or risks. Resolve material ambiguity before writing implementation tasks.
3. Use `spec-driven`: `proposal.md`, `design.md`, `specs/<capability>/spec.md`, `tasks.md` and `.openspec.yaml` under `openspec/changes/<change-id>/`. Reference any existing baseline capabilities under `openspec/specs/` before deciding ADDED versus MODIFIED requirements. Each requirement uses SHALL/MUST and has a `#### Scenario:` with WHEN/THEN. Keep implementation tasks unchecked.
4. Keep technical definitions in English. Allocate the next unused numeric spec ID for product/code/release-policy changes and add a thin link in `specs/`; requirements remain canonical in OpenSpec. Use the same ID for branch and PR. Pure documentation maintenance retains the [spec exemption](../../../ENGINEERING_RULES.md).
5. If file changes are needed, follow the branch selection in [code delivery](../humami-code-delivery/SKILL.md), restricted to local preparation. Do not turn a definition request into push, PR, merge or implementation.
6. Run `openspec validate <change-id> --strict` from the repo root if the CLI is available; if unavailable, report that limitation or use the official CLI through an authorized temporary installation. Check reference paths and proposal-to-capability consistency.

Deliver links to the definition and actual validation evidence. Artifact completion is not approval to apply. Implement only on a separate request. Archive only after implementation and acceptance; do not copy proposed specs into the current baseline prematurely.
