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

The [development skill](.agents/skills/humami-development/SKILL.md) owns implementation; [code delivery](.agents/skills/humami-code-delivery/SKILL.md) owns Git Flow and PR integration. Their steps are maintained in one place. [OpenSpec](.agents/skills/humami-feature-spec/SKILL.md) owns new change definitions; [release](.agents/skills/humami-release/SKILL.md) owns master-only production releases.

## Current priorities

See `PROGRESS.md` and active specs:

- API write hardening via secret headers
- Tests + CI baseline
- Simple CD safety flow
- Blog foundation
- SEO indexing baseline
- About page foundation
