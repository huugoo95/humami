# Contributing Guide

This project uses a lightweight but strict workflow to keep development reliable and easy to review.

## Branching model

- `master`: always stable
- Feature branches:
  - `feat/<short-name>`
  - `fix/<short-name>`
  - `chore/<short-name>`
  - `docs/<short-name>`

Examples:
- `feat/auth-refresh-token`
- `fix/login-timeout`
- `docs/onboarding-flow`

## Feature planning (required)

Before coding, create a feature note using `docs/features/TEMPLATE.md`.

Minimum required sections:
- Goal
- Scope (in/out)
- Acceptance criteria
- Risks
- Technical checklist

## Commit convention

Use clear, intent-based commits:
- `feat: ...`
- `fix: ...`
- `refactor: ...`
- `docs: ...`
- `test: ...`
- `chore: ...`

Guidelines:
- Prefer small, focused commits
- One intent per commit
- Avoid mixing refactor + feature in same commit unless necessary

## Pull requests

Every PR should include:
1. What changed
2. Why it changed
3. How to test
4. Risks and rollback notes

Suggested PR title format:
- `feat: ...`
- `fix: ...`
- `docs: ...`

## Quality gates (before merge)

- Lint passes
- Tests pass
- Build passes
- No obvious security/config regressions

## Progress tracking

Update `PROGRESS.md` while working:
- In progress
- Blocked
- Done
- Next steps
