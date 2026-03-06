# Branching and Commit Conventions

## Branch naming

- `feat/<spec-id>-<short-name>`
- `fix/<short-name>`
- `chore/<short-name>`
- `docs/<short-name>`

Examples:
- `feat/001-auth-session-timeout`
- `feat/001-auth-session-timeout-be`
- `feat/001-auth-session-timeout-fe`

## Commits

Use conventional-style prefixes:
- `feat:`
- `fix:`
- `docs:`
- `test:`
- `refactor:`
- `chore:`

Keep commits focused and atomic.

## PR scope

- One feature/spec per PR when possible.
- If split by FE/BE, reference same spec ID in both PRs.
