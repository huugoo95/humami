# Humami

Humami is a web platform for structured recipes and complete meal compositions.

## Repository structure

- `humami-backend/` — API, business logic, persistence
- `humami-web/` — frontend web app
- `docs/` — product/engineering documentation
- `specs/` — feature specs (spec-first workflow)

## Core docs

Start here:

- `CONTRIBUTING.md`
- `ENGINEERING_RULES.md`
- `docs/workflow.md`
- `docs/git-flow.md`
- `docs/HUMAMI.md`

## Development workflow (short)

1. Create/approve a spec in `specs/`.
2. Branch from `develop` (`feat/*`, `fix/*`, `docs/*`, etc.).
3. Implement with tests (TDD for non-trivial changes).
4. Open PR to `develop` with required template/checks.
5. Release via gitflow strategy (`release/*` / `hotfix/*`).

## Manual deploy and rollback

Production operations are manual and environment-driven:

- `scripts/deploy.sh`
- `scripts/rollback.sh`

Both scripts require an external env file (`DEPLOY_ENV_FILE`, default `/etc/humami/.env`) and never store secrets in the repository.

Example:

```bash
DEPLOY_ENV_FILE=/etc/humami/.env DEPLOY_BRANCH=master scripts/deploy.sh
DEPLOY_ENV_FILE=/etc/humami/.env scripts/rollback.sh
```

## Current priorities

See `PROGRESS.md` and active specs:

- API write hardening via secret headers
- Tests + CI baseline
- Simple CD safety flow
- Blog foundation
- SEO indexing baseline
- About page foundation

