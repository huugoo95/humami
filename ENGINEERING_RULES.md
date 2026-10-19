# ENGINEERING_RULES.md

This file is the engineering source of truth for Humami. If there is a conflict, this file wins.

## 1) Delivery model

- Trunk-based: `master` is the only long-lived branch and every merge deploys to production. Work on short branches and integrate through a PR with green CI (squash merge).
- Product features start from an OpenSpec change in `openspec/changes/` (numeric index in `specs/`). Fixes, refactors, dependency updates, CI/ops and docs need no spec: the PR description carries the why, evidence and rollback.
- Independent review (a separate agent) is required only for high-risk changes: infrastructure and deployment, authentication/security, data migrations, payments or personal data. Everything else relies on tests, CI and post-deploy verification.
- Production safety is automated: CI gates, exact-SHA images, smoke checks and automatic rollback on failed smoke checks.

Execution: [development](.agents/skills/humami-development/SKILL.md), [delivery](.agents/skills/humami-code-delivery/SKILL.md), [release](.agents/skills/humami-release/SKILL.md).

## 2) TDD (required)

For non-trivial code changes:

1. Write a failing test (RED)
2. Implement minimal code to pass (GREEN)
3. Refactor with tests green (REFACTOR)

Required in PR:
- test evidence (unit/integration/e2e as applicable)
- short note describing RED → GREEN intent

## 3) Architecture conventions

### Backend (`humami-backend`)

- Keep clear layering:
  - controllers/adapters
  - application/service logic
  - domain/model
  - persistence/repository
- Keep HTTP-specific concerns out of domain logic.
- Input validation at boundaries.
- Use MapStruct mappers for DTO/entity transformations (avoid ad-hoc manual mapping in services/controllers).

### Frontend (`humami-web`)

- Split by feature/domain, not by file type only.
- Keep API calls in dedicated data access modules/hooks.
- UI components should stay presentation-focused.

## 4) Library policy

- Prefer existing project libraries over introducing new ones.
- New dependency requires a short rationale in PR: why, alternatives considered, impact.
- Keep one library per concern when possible.

## 5) Code style and conventions

### Database naming (MongoDB)

- Collection names should use `snake_case` and plural form (e.g., `blog_posts`, `user_profiles`).
- Avoid mixed naming styles across collections.


- Respect repository formatter/linter config.
- `.editorconfig` defaults:
  - UTF-8
  - LF
  - final newline
  - 2 spaces for JS/TS/JSON/YAML/Markdown
  - 4 spaces for Java
- Naming:
  - clear, intent-revealing names
  - avoid single-letter names except tiny loop indices

## 6) Definition of Done

A task is done when:

- Behaviour is covered by tests that pass, and CI is green.
- The change is merged to `master` and the production deploy succeeded.
- Production was verified for the affected behaviour.
- Docs are updated if behaviour or process changed.

## 7) Pull Request requirements

Each PR states: what and why, test evidence, risk and rollback, and the spec link when one exists ([template](.github/pull_request_template.md)).
