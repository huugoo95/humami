# Humami

Humami is a web platform for structured recipes and complete meal compositions.

## Repository structure

- `humami-backend/` — API, business logic, persistence
- `humami-web/` — frontend web app
- `docs/` — product/engineering documentation
- `openspec/changes/` — active change definitions
- `specs/` — historical specs and numeric indexes
- `.agents/skills/` — task procedures loaded on demand
- `ops/` — decisions, incidents and specialist profiles

## Core docs

Start here:

- `CONTRIBUTING.md`
- `ENGINEERING_RULES.md`
- [Task routing](AGENTS.md)
- `docs/HUMAMI.md`

## Development workflow

Trunk-based on `master`: short branch → PR → green CI → squash merge → automatic production deploy with smoke checks and automatic rollback. See [AGENTS.md](AGENTS.md) for how we work and what needs the owner's approval, and [code delivery](.agents/skills/humami-code-delivery/SKILL.md) for the steps. There is no dev environment; test locally.

## Current priorities

See `PROGRESS.md` and `docs/HUMAMI.md`.
