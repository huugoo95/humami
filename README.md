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

## Current priorities

See `PROGRESS.md` and active specs:

- API write hardening via secret headers
- Tests + CI baseline
- Simple CD safety flow
- Blog foundation
- SEO indexing baseline
- About page foundation

