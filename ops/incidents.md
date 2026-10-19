# Incident Log

Use this file for short incident records and postmortem-lite notes.

## Template
- Date/time:
- Symptom:
- Impact:
- Root cause:
- Fix:
- Prevention:

---

## 2026-03-19 – post-deploy transient 502 on `/`
- Symptom: Smoke check failed on `/` with HTTP 502 immediately after deploy.
- Impact: Short transient frontend unavailability through nginx.
- Root cause: nginx upstream cached old frontend container IP after recreate.
- Fix: Restarted `nginx` container, then smoke checks passed.
- Prevention: After container recreation, include explicit nginx restart in deploy runbook (or use dynamic resolver pattern).

## 2026-09-24 — Initial dev image publication blocked
- Symptom: First ARM64 build omitted the root Maven wrapper; subsequent publication failed with write_package, then server pull rejected its existing registry credential.
- Impact: Dev activation delayed; production application containers remained unchanged.
- Fix: PR63 supplies the wrapper; ARM64 builds run on relevant PRs. User approved Actions Write on the two existing private packages and GitHub CLI read:packages renewal.
- Prevention: Keep PR image builds and verify publisher/reader permissions before future deployments; never assume an old server credential still works.

## 2026-10-18 — Release 0.5.2 deploy job failed on stale readiness timeout
- Symptom: Production deployment run 39168330343 (master 57248d6) recreated all containers, then failed with `[TIMEOUT] /api/meals… did not become ready within 90s (last HTTP 502)`. Release 0.5.1 (run 36266408122) failed the same way.
- Impact: `/api/*` returned 502 and `/guias/*` returned 404 for about 3 minutes, until the backend finished starting (ready around 21:39:46 UTC). `/` and `/meals` stayed at 200.
- Root cause: the workflow copies `scripts/deploy-images.sh` and `scripts/smoke-prod.sh` into the server app root, but runs `./scripts/deploy-images.sh`. The server therefore runs stale copies in `scripts/` that still wait 90s, so spec 028's 480s readiness window never took effect. Backend cold start (about 3 minutes) is longer than 90s.
- Fix: deployment accepted after manual verification. `scripts/smoke-prod.sh https://humami.es` passed, the new frontend CSS bundle is served, and the guide button was checked in a browser. Images: ghcr.io/huugoo95/humami-{backend,frontend}:57248d6da76b0ade2acbd1134b267d96a5a03464. Previous known-good: db1f901 (frontend sha256:2c2e7df1…, backend sha256:8a83695a…).
- Prevention: spec 031 (PR #84) copies the scripts to the executed path and verifies their checksums; confirmed in run 39240948149.

## 2026-10-19 — Release 0.5.3 deploy aborted: production disk full
- Symptom: Production deployment run 39239363128 (master 7c105ce) passed the script checksums, then failed while extracting the new frontend image: `no space left on device` under `/var/lib/containerd`.
- Impact: none for users. No container was recreated, and production kept serving 0.5.2 (all smoke endpoints 200). Release 0.5.3 was delayed.
- Root cause: every deployment pulls new SHA-tagged images and nothing pruned the old ones. The 19 GB root disk was 97% full (624 MB free). The same host runs dev.humami.es.
- Fix: hotfix spec 033 (PR #87, released as 0.5.4, master 6e58547). Before pulling, `deploy-images.sh` now logs disk usage, prunes images no container uses and the build cache, and requires 3072 MB free. Run 39240948149 reclaimed 1.97 GB (57% used, 7.9 GB free) and deployed successfully.
- Prevention:
  - Pruning now happens on every deploy.
  - Follow-ups: the host disk is small (19 GB) for prod plus dev; the frontend runtime image ships full `node_modules` (Next `standalone` output would shrink it); dev deploys time out on a slow backend start (173 s, 12 s MongoDB round-trip).
