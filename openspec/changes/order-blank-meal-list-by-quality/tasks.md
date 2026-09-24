## 1. Backend implementation

- [x] 1.1 Add a failing regression for quality-descending blank-query ordering
  across pages and ID ordering for equal scores.
- [x] 1.2 Sort blank-query MongoDB pages by `quality.score` descending then
  persisted ID ascending while retaining existing eligibility predicates.
- [x] 1.3 Cover zero-threshold missing/null scores and preserve nonblank search
  relevance behavior.

## 2. Verification and delivery

- [x] 2.1 Run targeted and full relevant backend checks, including
  MongoDB-backed sorting evidence.
- [x] 2.2 Obtain independent review and resolve blocking findings.
- [ ] 2.3 Deliver the implementation through a PR to `develop`; no production
  deployment.
- [ ] 2.4 Archive only after acceptance.
