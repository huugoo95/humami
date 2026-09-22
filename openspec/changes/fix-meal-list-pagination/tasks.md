## 1. Backend regression and implementation
- [x] 1.1 Add failing regressions for filtering before pagination and correct totals across multiple pages.
- [x] 1.2 Implement database-side eligibility, matching count and ID-ordered pagination for blank queries, including legacy effective-zero behavior.
- [x] 1.3 Preserve search relevance and add stable ID tie-breaking before slicing.
- [x] 1.4 Cover threshold equality, missing/null quality scores, empty/out-of-range pages and parameter normalization, with MongoDB-backed query verification.

## 2. Verification and delivery
- [x] 2.1 Run relevant backend checks; verify catalogue and sitemap compatibility without unnecessary frontend changes.
- [x] 2.2 Obtain independent review of the implementation and record evidence against acceptance scenarios.
- [x] 2.3 Deliver through a PR to develop under the delivery gates; no production deploy.
- [ ] 2.4 Archive only after implementation is accepted.

## 3. Approved validation blockers
- [x] 3.1 Correct the About authentication test method and isolate S3 context-test configuration.
- [x] 3.2 Run the full backend suite with MongoDB and package successfully.
- [x] 3.3 Add and verify backend CI on the PR, including MongoDB integration tests.
- [x] 3.4 Independently review the blocker fixes before integration.
