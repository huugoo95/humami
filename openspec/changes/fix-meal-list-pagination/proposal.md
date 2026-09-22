# Spec 017 — Reliable meal listing pagination

## Why
The public recipe catalogue (`GET /api/meals`) paginates unfiltered MongoDB records and then removes meals below the quality threshold. This produces short or empty intermediate pages, counts hidden meals in totalPages and reports the current page size as totalItems. Neither the default listing nor relevance ties have an explicit stable order.

## Goal and priority
Priority: proposed Next, a correctness fix to the existing catalogue, without changing the product roadmap. As a visitor, I want complete, predictably ordered pages of eligible recipes so that I can browse all available results.
Success metric: for an unchanged dataset, every eligible ID appears exactly once across all pages; every non-final page is full; totalItems equals the eligible result count. Verify with deterministic fixtures, not invented usage targets.

## What Changes
- Apply the quality predicate before offset/limit and count the same eligible set.
- Use ascending ID for the default listing and ascending ID to break equal relevance scores. This is a technical stability rule, not recency or editorial ranking.
- Preserve search scoring, response shape, default threshold and pagination normalization.

## Capabilities
### New Capabilities
- `meal-list-pagination`: eligibility, deterministic ordering and accurate pagination metadata for GET /api/meals.

### Modified Capabilities
None: no current OpenSpec baseline exists. Historical spec 015 supplies quality-filter context; this change does not replace its scoring definition.

## Impact and constraints
Backend repository/service and regression tests; frontend verification only unless a compatibility defect is found. No new dependency, schema migration, quality backfill, live publication, release or deployment. Deliverable is this definition only; implementation requires a separate request. Deadline: not provided.

## Approved delivery-blocker follow-up
On 2026-09-22 the user requested correction of the reported blockers. Scope now includes correcting the About upload authentication test to use the actual PUT route, supplying fictitious S3 configuration only to the context test, and adding a backend CI workflow with isolated MongoDB. Production behavior and credentials remain unchanged. Acceptance: the full suite and package pass locally and on the PR with MongoDB integration tests enabled. No branch protection changes are included.
