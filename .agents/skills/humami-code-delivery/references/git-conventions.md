# Branching and Commit Conventions

Use the parent [delivery skill](../SKILL.md) for the branch lifecycle; this reference owns branch and commit naming.

## Core branches

- `master` (stable / production-ready)
- `develop` (integration for upcoming release)

## Branch naming

- `feat/<spec-id>-<short-name>` (from `develop`)
- `fix/<short-name>` (from `develop`)
- `release/<version>` (from `develop`)
- `hotfix/<short-name>` (from `master`)
- `chore/<short-name>` (from `develop`)
- `docs/<short-name>` (from `develop`, except production-hotfix documentation)

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
- Feature/fix/chore and ordinary docs branches target `develop`.
- Documentation tied to a production hotfix branches from `master`, targets `master` via PR and is back-merged into `develop`.
- Release/hotfix branches target `master` and are back-merged into `develop` through PRs.
