# Ops Handbook (Humami)

This directory is the operational memory of the project.

## Why this exists
When work spans backend, frontend, deploy, recipes, and SEO, context gets fragmented.
This folder keeps execution knowledge in one place so we don't rely on chat history or personal memory.

## Tracks (work lanes)
Every task should be assigned to exactly one primary track:

1. **BE/API** – contracts, models, endpoints, backend logic
2. **FE/UX** – UI, frontend behavior, API integration from web
3. **Infra/Deploy** – server, docker, nginx, release/deploy/rollback
4. **Data/Recipes** – recipe content operations and publishing flow
5. **SEO/Growth** – indexing, metadata, structured data, content SEO

If a task touches multiple tracks, pick one primary owner track and list dependencies.

## Task line format (minimum)
Use this one-liner before execution:

`[TRACK] objective | done-criteria | deadline`

Example:
`[Infra/Deploy] Deploy typo-tolerant meal search to prod | smoke-prod all 200 | today 18:00 CET`

## What goes in repo vs outside

### In repo (safe, team-shared)
- Runbooks
- Checklists
- Non-sensitive operational decisions
- Incident writeups without secrets
- Repeatable scripts without credentials

### Outside repo (private/sensitive)
- PEM keys, tokens, passwords
- Real credentials and secret values
- Sensitive internal host details not required for team work

## Files
- `runbook-deploy.md`
- `runbook-recipes.md`
- `runbook-seo.md`
- `decisions.md`
- `incidents.md`
- `task-template.md`
