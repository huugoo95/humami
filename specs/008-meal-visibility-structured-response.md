# Spec 008: meal-visibility-structured-response

- **Status:** Draft
- **Owner:** Hugo + tenacitas
- **Created:** 2026-03-12

## 1) Problem

`GET /api/meals/{id}` currently returns recipes, but meal detail UX needs better readability:
- ingredients should be visible first,
- ingredients must be separated per recipe/preparation,
- then each recipe should show its own details and steps.

## 2) Goal and metric

- Goal: deliver a structured meal detail response and UI aligned with the new reading flow.
- Success metric: meal detail page renders ingredients grouped by recipe first, then recipe sections with steps.

## 3) Scope

### In scope
- Extend meal detail response with grouped ingredients section.
- Update web meal detail page to follow: grouped ingredients -> recipe detail blocks.
- Keep compatibility with existing `recipes[]` consumers.

### Out of scope
- Full API versioning strategy.
- New meal creation flow changes.

## 4) Acceptance criteria

1. `GET /api/meals/{id}` includes a grouped ingredient structure by recipe.
2. Meal page (`/meals/[id]`) shows grouped ingredients first.
3. Meal page then shows each recipe details and instructions.
4. Existing recipe-level ingredient data still available.

## 5) Rollout

- Implement backend + frontend in same branch.
- Validate against real example meal:
  - `https://humami.es/meals/67ce0042694737444ba994c2`
