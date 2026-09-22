## 1. Backend regression and implementation
- [x] 1.1 Add failing regressions for filtering before pagination and correct totals across multiple pages.
- [x] 1.2 Implement database-side eligibility, matching count and ID-ordered pagination for blank queries, including legacy effective-zero behavior.
- [x] 1.3 Preserve search relevance and add stable ID tie-breaking before slicing.
- [x] 1.4 Cover threshold equality, missing/null quality scores, empty/out-of-range pages and parameter normalization, with MongoDB-backed query verification.

## 2. Verification and delivery
- [x] 2.1 Run relevant backend checks; verify catalogue and sitemap compatibility without unnecessary frontend changes.
- [x] 2.2 Obtain independent review of the implementation and record evidence against acceptance scenarios.
- [ ] 2.3 Deliver through a PR to develop under the delivery gates; no production deploy.
- [ ] 2.4 Archive only after implementation is accepted.
