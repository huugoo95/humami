# Branching and Commit Conventions

See `docs/git-flow.md` for full branch lifecycle.

## Core branches

- `master` (stable / production-ready)
- `develop` (integration for upcoming release)

## Branch naming

- `feat/<spec-id>-<short-name>` (from `develop`)
- `fix/<short-name>` (from `develop`)
- `release/<version>` (from `develop`)
- `hotfix/<short-name>` (from `master`)
- `chore/<short-name>`
- `docs/<short-name>`

Examples:
- `feat/001-auth-session-timeout`
- `feat/001-auth-session-timeout-be`
- `release/0.4.0`
- `hotfix/login-token-expiry`

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
- Feature/fix branches target `develop`.
- Release/hotfix branches follow `docs/git-flow.md` merge rules.
