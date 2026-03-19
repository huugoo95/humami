# Contributing Guide

This project follows a spec-first workflow with explicit engineering rules.

## Core references (read first)

- `ENGINEERING_RULES.md`
- `docs/workflow.md`
- `docs/git-flow.md`
- `docs/branching.md`
- `docs/architecture.md`
- `docs/approved-libraries.md`

## Branching model

- `master`: always stable
- `develop`: integration branch for upcoming release
- Branches:
  - `feat/<spec-id>-<short-name>`
  - `fix/<short-name>`
  - `release/<version>`
  - `hotfix/<short-name>`
  - `chore/<short-name>`
  - `docs/<short-name>`

## Feature planning (required)

Before coding, create a spec from `specs/000-template.md`.

Required sections:
- Problem
- Goal and metric
- Scope in/out
- Acceptance criteria (Given/When/Then)
- Risks/dependencies

## Pull requests

Every PR must:
1. Reference spec ID
2. Fill `.github/pull_request_template.md`
3. Include test evidence
4. Include risks and rollback notes

Direct commits to `develop` and `master` are not allowed.

## Quality gates (before merge)

- Lint passes
- Tests pass
- Build passes
- PR compliance checklist completed

## Progress tracking

Update `PROGRESS.md` while working:
- In progress
- Blocked
- Done
- Next steps
