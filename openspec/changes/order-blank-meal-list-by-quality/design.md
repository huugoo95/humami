# Design

## Current behavior

`MealController` delegates `GET /api/meals` to `MealServiceImpl.getPaged`.
For a blank query, the service creates a `PageRequest` sorted by `id`
ascending, then asks the repository for the quality-eligible page. This is
stable but does not represent catalogue quality. Nonblank queries already
filter by quality and sort by text relevance, with ID as a stable tie-breaker.

## Decision

1. For a null, blank or whitespace-only query, create the pageable with
   `quality.score` descending then `id` ascending. Keep pagination in the
   database and keep the existing repository eligibility predicates.
2. The effective score rule is unchanged: missing quality, missing score or a
   null score is 0.0. With the default threshold of 0.5 those records are not
   eligible; with a threshold of 0 they can be returned after all positive
   scores, then ordered by ID.
3. Preserve the nonblank query flow exactly: its primary sort is existing
   fuzzy relevance descending and its secondary sort is persisted ID
   ascending. Do not repurpose quality as a search-ranking factor.
4. Preserve `items`, `page`, `limit`, `totalItems` and `totalPages`, including
   existing normalization and the 48-item maximum page size. The frontend
   consumes the API order unchanged and needs no change.

## Data, API and security

No data is modified and no fields or endpoints are added. MongoDB's existing
`quality.score` field is used only for sorting. No credentials, access rules
or collection/index migrations change.

## Verification and rollback

Begin with a failing regression that asserts quality-descending blank-query
ordering across a page boundary and deterministic ascending IDs for equal
scores. Retain coverage for default threshold, zero-threshold legacy records,
metadata and search behavior. Include MongoDB-backed evidence that repository
sorting is applied before pagination. Run relevant backend tests and the full
backend verification command. Reverting the implementation commit restores the
previous ID-first order; no data rollback is needed.
