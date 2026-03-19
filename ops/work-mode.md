# Work Mode (Daily Ops)

Single operational guide for day-to-day execution.

## 1) Intake (required)
Start every significant task with:

`[TRACK] objective | done-criteria | deadline`

Tracks:
- BE/API
- FE/UX
- Infra/Deploy
- Data/Recipes
- SEO/Growth

### What is a "significant task"?
A task is significant if at least one is true:
- creates/modifies/deletes project code or docs in repo
- affects production/staging infrastructure
- requires a PR
- takes more than ~15 minutes
- needs explicit validation evidence (tests/smoke/logs)

## 2) Execution mode
- Small/simple task -> execute directly.
- Medium/complex or multi-domain task -> use split execution (implementer + reviewer).

## 3) Validation before close
- Acceptance criteria checked
- Evidence captured (tests/logs/smoke/URLs/screenshots as applicable)
- Workflow respected (branch + PR + spec reference)
- If durable decision: update `ops/decisions.md`
- If incident/anomaly: update `ops/incidents.md`

## 4) Completion report format
- Context
- Action
- Result
- Next

Keep it short and evidence-based.
