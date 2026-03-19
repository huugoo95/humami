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
