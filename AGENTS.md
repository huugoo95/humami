# Humami

## Map
- `humami-web/`: Next.js frontend; `humami-backend/`: Spring Boot API.
- `docs/`: shared product, brand and architecture knowledge.
- `openspec/changes/`: product feature definitions; `specs/`: historical specs and numeric indexes.
- `.agents/skills/`: procedures loaded on demand; `ops/`: decisions and operating records.
- `scripts/`: executable tools; do not duplicate their logic in instructions.

## How we work (decision 2026-10-19)
- Side project, one owner, AI does the coding. Optimize for speed with automated safety, not ceremony.
- Trunk-based: `master` is the only long-lived branch. Short branch → PR → green CI → squash merge → automatic production deploy with smoke checks and automatic rollback. No `develop`, release, hotfix or back-merge branches. No dev environment: test locally.
- **Standing authorization:** merge to `master` and deploy to production without asking when CI is green and the change is within the request. Verify production afterwards.
- **Always ask first:** permanent deletion of data, files or resources; new paid resources or anything that raises cost; DNS, domain, IAM or account changes; secrets; sending messages or publishing content on the owner's behalf.

## Common rules
- Before edits, inspect branch, local changes and active worktrees; preserve user work.
- Select the relevant procedure below before executing; do not read every linked file.
- A definition-only request stops at its artifacts.
- Distinguish local changes, committed, pushed, merged and deployed states in reports.
- Never infer the deployed version from a branch name or expose credentials.

## Select a procedure
- Define a product feature: [feature spec](.agents/skills/humami-feature-spec/SKILL.md).
- Implement or fix: [development](.agents/skills/humami-development/SKILL.md).
- Commit, PR, merge: [code delivery](.agents/skills/humami-code-delivery/SKILL.md).
- High-risk change review: [code review](.agents/skills/humami-code-review/SKILL.md).
- Deploy, verify or recover production: [release](.agents/skills/humami-release/SKILL.md).
- Prepare recipes: [meal authoring](.agents/skills/humami-meal-authoring/SKILL.md).
- Draft blog content: [editorial](.agents/skills/humami-editorial/SKILL.md).
- Publish site content: [content publication](.agents/skills/humami-content-publish/SKILL.md).

## Coordination
- Handle work directly; consult [delegation](ops/agents/README.md) only when splitting large work.
- Temporary subagents are internal workers, not new user-owned tasks or isolated checkouts.
- Record durable decisions in `ops/decisions.md` and incidents in `ops/incidents.md`.
