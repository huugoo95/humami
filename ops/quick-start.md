# Quick Start (Ops)

Use this flow for any new task.

## 1) Classify the task
Pick one primary track:
- BE/API
- FE/UX
- Infra/Deploy
- Data/Recipes
- SEO/Growth

## 2) Write the one-liner
`[TRACK] objective | done-criteria | deadline`

Examples:
- `[BE/API] Add typo-tolerant meal search fallback | tests pass + query 'coockies' returns cookies meal | today 19:00 CET`
- `[Infra/Deploy] Deploy latest develop to prod | smoke-prod all 200 | today 19:30 CET`
- `[SEO/Growth] Improve meal detail metadata | title+description updated on top 10 pages | this week`

## 3) Choose execution mode
- Small/simple: execute directly
- Medium/complex: split with sub-agents (executor + reviewer)

## 4) Validate before closing
- Acceptance criteria met
- Evidence captured (logs/tests/smoke/URLs)
- Update `ops/decisions.md` if a durable decision was made
- Update `ops/incidents.md` if an incident occurred

## 5) Report result in 4 lines
- Context
- Action
- Result
- Next
