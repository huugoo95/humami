# ENGINEERING_RULES.md

This file is the engineering source of truth for Humami. If there is a conflict, this file wins.

## 1) Delivery model

- Every change starts from a written feature spec in `specs/`.
- Every PR must reference one spec ID.
- No direct commits to `master`.

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

A task is done only when:

- [ ] Spec acceptance criteria are covered
- [ ] Tests added/updated and passing
- [ ] Lint/format checks pass
- [ ] Docs updated (if behavior changed)
- [ ] PR template fully completed

## 7) Pull Request requirements

Each PR must include:

- Spec ID + link
- Scope summary (backend/frontend)
- Test evidence
- Risks + rollback note
- Compliance checklist (`.github/pull_request_template.md`)
