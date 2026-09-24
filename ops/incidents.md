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
