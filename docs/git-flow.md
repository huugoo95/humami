# Git Flow Strategy (Humami)

Humami uses a pragmatic Git Flow model adapted to our spec-first workflow.

## Long-lived branches

- `master`: production-ready, always stable.
- `develop`: integration branch for upcoming release.

## Supporting branches

- `feat/<spec-id>-<short-name>`: new feature work (from `develop`, merge back to `develop`).
- `fix/<short-name>`: non-urgent fixes for upcoming release (from `develop`, merge back to `develop`).
- `release/<version>`: release hardening branch (from `develop`, merge to `master` and back to `develop`).
- `hotfix/<short-name>`: urgent production fix (from `master`, merge to `master` and `develop`).
- `docs/<short-name>`: documentation-only updates (from `develop` unless tied to production hotfix context).
- `chore/<short-name>`: maintenance/dev tooling updates.

## Required flow

1. Create/update feature spec in `specs/`.
2. Branch from `develop` using conventions.
3. Open PR into `develop`.
4. For releases, branch `release/<version>` from `develop`.
5. After verification, merge `release/<version>` into `master` and back-merge to `develop`.
6. For urgent prod bugs, use `hotfix/*` from `master`, then merge into both `master` and `develop`.

## Branch protections (recommended)

- Protect `master` and `develop`.
- Require PR + review + required checks.
- Disallow direct pushes.

## Notes

- One spec/feature per PR whenever possible.
- If FE/BE split is needed, use matching spec ID in both branches and PRs.
