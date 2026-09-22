# Humami

## Map
- `humami-web/`: Next.js frontend; `humami-backend/`: Spring Boot API.
- `docs/`: shared product, brand and architecture knowledge.
- `openspec/changes/`: active definitions; `specs/`: historical specs and numeric indexes.
- `.agents/skills/`: procedures loaded on demand; `ops/`: decisions and operating records.
- `scripts/`: executable tools; do not duplicate their logic in instructions.

## Common rules
- Before edits, inspect branch, local changes and active worktrees; preserve user work.
- Select the relevant procedure below before executing; do not read every linked file.
- For significant work, use [intake and closure](.agents/skills/humami-development/references/task-lifecycle.md).
- A definition-only request stops at its artifacts; implementation needs a separate request.
- Distinguish local changes, committed, pushed, merged and deployed states in reports.
- Never infer the deployed version from a branch name or expose credentials.
- No direct implementation on `develop` or `master`; preserve project branch naming.

## Select a procedure
- Define a feature: [feature spec](.agents/skills/humami-feature-spec/SKILL.md).
- Implement or fix: [development](.agents/skills/humami-development/SKILL.md).
- Review changes: [code review](.agents/skills/humami-code-review/SKILL.md).
- Commit, push or integrate: [code delivery](.agents/skills/humami-code-delivery/SKILL.md).
- Prepare recipes: [meal authoring](.agents/skills/humami-meal-authoring/SKILL.md).
- Draft blog content: [editorial](.agents/skills/humami-editorial/SKILL.md).
- Publish site content: [content publication](.agents/skills/humami-content-publish/SKILL.md).
- Prepare/deploy a release: [release](.agents/skills/humami-release/SKILL.md).

## Coordination
- Handle small work directly; consult [delegation](ops/agents/README.md) when splitting work.
- Temporary subagents are internal workers, not new user-owned tasks or isolated checkouts.
- Persist decisions and evidence outside chat; report the actual delivery state and blockers.
