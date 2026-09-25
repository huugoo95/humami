# Design

## Current behavior and responsibilities
MealController delegates GET /api/meals to MealServiceImpl.getPaged. The blank-query path uses unsorted PageRequest and filters each page afterward. The search path filters before scoring and slicing, but relevance ties inherit repository order. The frontend requests limit=12 and renders items unchanged. The sitemap also consumes this endpoint.

## Decisions
1. For blank or whitespace-only query, apply quality eligibility in MongoDB before pagination. Fetch the page ordered by `_id` ascending and count with the identical predicate. Do not introduce an all-record in-memory scan for this path.
2. Preserve the current rule: absent quality or absent/null score has effective score 0.0. Eligibility is effective score >= minQualityScore; default 0.5. The database predicate must include legacy missing/null scores when threshold <= 0. Tests must cover this, rather than silently changing visibility.
3. For nonblank queries preserve normalization, fuzzyScore weights and score > 0 eligibility. Sort by relevance descending, then persisted ID ascending, before slicing. The tie-break comparator follows normal Spring ID persistence: BSON strings precede ObjectIds, and valid hexadecimal IDs map to ObjectIds. Manually inserted hexadecimal BSON string IDs cannot be distinguished after entity mapping; no such data has been identified. No new ranking algorithm or search endpoint changes.
4. ID ordering provides stability without requiring timestamps for legacy records. It is not a creation-date guarantee. No user-selectable sort control is introduced.
5. Keep the JSON fields items, page, limit, totalItems and totalPages. Normalize page to >=1 and limit to 1..48. totalItems is the count of all eligible results; totalPages=max(ceil(totalItems/limit),1). An out-of-range page remains the requested normalized page, with empty items and correct totals.

## Frontend and contracts
No client-side sorting or filtering. Verify catalogue pagination and sitemap consumers against corrected totals. Page size remains 12 in the catalogue; the last page may be shorter. Existing parameter validation outside these cases is unchanged.

## Risks, dependencies and security
Requires the existing Spring Data MongoDB repository and quality metadata; no new services or dependencies. A different default order is intentionally visible. Offset pagination does not provide snapshot consistency across concurrent writes; exactly-once traversal applies only to unchanged datasets. Separate count/page reads may observe concurrent mutations; snapshot transactions and cursor pagination are out of scope. Inspect query cost, but do not add speculative indexes or migrations without evidence. Public read access and write authentication remain unchanged.

## Verification and rollout
Add failing regression tests for mixed quality records across multiple pages, threshold equality, legacy missing scores, empty sets, out-of-range pages, normalization and tied search scores. Include MongoDB-backed coverage proving filter, count and ordering semantics, beyond mocks of a prefiltered repository result. Run relevant backend checks and verify frontend/sitemap compatibility. Deliver through develop after review; no deployment is authorized by this definition. Revert the implementation commit to roll back; no data rewrite is required.
