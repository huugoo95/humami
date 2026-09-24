# Spec 022 — Rank the recipe catalogue by quality

## Why

The public recipe catalogue already applies the quality threshold before
pagination, but a blank query then sorts by persisted MongoDB ID. As a result,
high-quality recipes such as Carbonara (score 0.97) can appear on the last
page solely because of their ID. Visitors do not see the strongest recipes
first when browsing without a search.

## Goal and priority

Priority: high product-catalogue correctness. As a visitor, I want the
unsearched recipe listing to present the highest-quality eligible recipes
first so that the catalogue's default view reflects the quality model.

Success metric: for an unchanged dataset, each blank-query page sequence is
ordered by effective quality score descending, with a deterministic order for
equal scores. Verify this with regression and MongoDB-backed tests.

## Scope

- Rank blank and whitespace-only `GET /api/meals` results by effective quality
  score descending before pagination.
- Retain persisted ID ascending as the deterministic tie-breaker.
- Preserve quality eligibility, pagination metadata, request normalization,
  response shape and nonblank search relevance ordering.

## Non-goals

- No user-selectable sort control, score display, score recalculation, data
  backfill, schema migration, new index, frontend sorting or deployment.

## User story

As a catalogue visitor, when I open the recipe list without searching, I see
the highest-quality eligible recipes before lower-quality recipes.

## Risks and dependencies

The change depends on existing `quality.score` data and Spring Data MongoDB
sorting. Records with a missing/null score retain their existing effective
score of 0.0, so they have a predictable position when included by a zero or
negative threshold. The visible default ordering changes intentionally; a
single-field sort may use an in-memory MongoDB sort until a measured need for
an index exists. Public read access, write authentication and search ranking
remain unchanged.

## Delivery

Backend implementation and regression coverage are required. Deliver through a
PR to `develop`; production deployment is out of scope.
