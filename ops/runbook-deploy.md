# Runbook: Deploy (Humami)

## Scope
Production deploy on `ubuntu@humami.es` for repository `/home/ubuntu/apps/humami`.

## Preconditions
- Changes merged to target branch (`develop` currently)
- SSH access available
- No active incident blocking deploy

## Standard deploy
1. Connect and update code:
   - `cd /home/ubuntu/apps/humami`
   - `git fetch origin`
   - `git checkout develop`
   - `git pull --ff-only origin develop`
2. Build and start containers:
   - `docker compose --profile prod up -d --build`
3. If frontend was recreated, restart nginx to refresh upstream targets:
   - `docker compose --profile prod restart nginx`
4. Run smoke checks:
   - `./scripts/smoke-prod.sh https://humami.es`

## Success criteria
- `/` => 200
- `/meals` => 200
- `/api/meals?query=&page=1&limit=5` => 200

## Fast rollback (manual)
1. Identify last known good commit
2. `git checkout <good-commit-or-tag>`
3. `docker compose --profile prod up -d --build`
4. `docker compose --profile prod restart nginx`
5. Re-run smoke checks

## Notes
- First deploy after image/cache changes can take longer.
- Keep incident notes in `ops/incidents.md`.
