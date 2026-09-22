# AI workflow prerequisites and follow-ups

These are pending work items, not implemented capabilities. Each behavior change needs its own OpenSpec definition before implementation.

| Work | Completion evidence | Until then |
| --- | --- | --- |
| CI and GitHub protection enforcement | Working frontend/backend checks, required-check rules verified remotely, AI-review policy reconciled with actual approval requirements | No autonomous merge when required checks are missing or rules are unknown |
| Meal validator minimum and malformed input handling | Regression tests for fewer than two steps and malformed supported payloads; valid examples remain accepted | Authoring skill checks the minimum in addition to running the current validator |
| Publishing reliability | Defined retry/reconciliation contract and tested handling of partial writes; separate spec for any client/API changes | Persist created ID, retry only known failed steps, stop on unresolved creation outcomes |

Production/staging topology and running image versions are discovered for an actual release; no staging service or CI run is created by these documents.
