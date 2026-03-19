# Spec 012: about-db-persistence-and-write

- **Status:** Draft
- **Owner:** Hugo + tenacitas
- **Created:** 2026-03-19

## 1) Problem
About content is currently sourced from application properties, which is not suitable for frequent updates and does not align with storing dynamic content (including photo URL) outside the repo.

## 2) Goal and metric
- Goal: persist About content in MongoDB and expose read/write API paths.
- Success metric:
  - `GET /api/about` reads from DB
  - `PATCH /api/about` updates DB content with write-secret auth
  - response includes `title`, `story[]`, `photoUrl`, `updatedAt`

## 3) Scope

### In scope
- Add MongoDB model `AboutEntity` and repository.
- Migrate `AboutService` from properties-only source to DB persistence.
- Add `PATCH /api/about` endpoint.
- Protect write endpoint via existing write auth interceptor.
- Add/update controller + service tests.

### Out of scope
- Admin UI for editing About.
- Multi-record pages/CMS model.

## 4) User stories
- As owner, I want to update About text and photo URL without storing private images in repo.

## 5) Acceptance criteria (Given / When / Then)
1. Given no about record exists
   When `GET /api/about` is called
   Then service seeds default `main` record and returns it.

2. Given an about record exists
   When `PATCH /api/about` is called with valid secret
   Then record is updated and `updatedAt` is refreshed.

3. Given missing/invalid write secret
   When `PATCH /api/about` is called
   Then request is rejected by write auth interceptor.

## 6) Edge cases
- Empty or blank story lines are filtered in updates.
- Null fields in patch request do not overwrite existing values.

## 7) Technical notes
- Collection: `about_pages`
- Record id: `main` (single-page model)

## 8) Risks and dependencies
- Risk: accidental overwrite of entire story content from patch payload.
- Mitigation: field-level patch behavior and tests.

## 9) Rollout and rollback
- Rollout: merge + deploy backend.
- Rollback: revert PR; GET endpoint returns properties-backed fallback from previous version.
