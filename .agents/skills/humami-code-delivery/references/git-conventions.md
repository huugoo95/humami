# Branching and Commit Conventions

## Branches

- `master` is the only long-lived branch. Merges that touch the application, Compose, nginx or deploy scripts deploy automatically; after an automatic rollback production runs the previous SHA until a fix lands.
- Short-lived work branches from `master`, merged back by squash PR:
  - `feat/<spec-id>-<short-name>` for product features with a spec
  - `fix/<short-name>`, `chore/<short-name>`, `docs/<short-name>` for everything else

## Commits

Conventional prefixes: `feat:`, `fix:`, `docs:`, `test:`, `refactor:`, `chore:`, `ci:`. Keep commits focused.

## PR scope

One change per PR. Prefer small PRs: each one is a production deployment.
