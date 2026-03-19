# Spec 011: about-endpoint-get

- **Status:** Draft
- **Owner:** Hugo + tenacitas
- **Created:** 2026-03-19

## 1) Problem
About content and personal photo URL should not depend on frontend hardcoded copy or repository-stored assets.

## 2) Goal and metric
- Goal: provide a backend read endpoint for About content.
- Success metric: frontend and external clients can read about title/story/photoUrl/updatedAt from one API route.

## 3) Scope

### In scope
- `GET /api/about` endpoint.
- Response fields: `title`, `story[]`, `photoUrl`, `updatedAt`.
- Basic controller/service test coverage.

### Out of scope
- Admin write endpoint for About content.
- Persistence model in database.
- Auth changes for read endpoint.

## 4) User stories
- As a visitor, I want About content to be consistent and easy to update, so that brand story is clear.

## 5) Acceptance criteria (Given / When / Then)
1. Given a client requests `GET /api/about`
   When endpoint responds
   Then payload includes `title`, `story`, `photoUrl`, and `updatedAt`.

2. Given endpoint tests run
   When validation executes
   Then controller returns 200 and mapped fields are present.

## 6) Edge cases
- Invalid configured `updatedAt` value should not crash endpoint.
- Empty `photoUrl` is valid.

## 7) Technical notes
### Backend
- Add About controller + service + response DTO.
- Use properties-backed initial content for v1.

### Frontend
- Optional future consumption by `/our-story` page.

### API/Contract
- Route: `GET /api/about`
- Status: `200 OK`

## 8) Risks and dependencies
- Risk: stale content if properties are not updated.
- Mitigation: future write/admin path or DB-backed content.

## 9) Rollout and rollback
- Rollout: merge and deploy backend.
- Rollback: revert endpoint files and restore previous frontend hardcoded content behavior.
