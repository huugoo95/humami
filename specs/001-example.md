# Spec 001: Session timeout warning

- **Status:** Draft
- **Owner:** Team
- **Created:** 2026-03-06

## 1) Problem

Users can be logged out unexpectedly with no warning, causing data loss and confusion.

## 2) Goal and metric

- Goal: warn users before session expiration and offer extension.
- Success metric: reduce unexpected session-expiry support complaints by 40%.

## 3) Scope

### In scope
- Backend endpoint to refresh session if valid.
- Frontend warning modal shown before expiry.
- Basic analytics event for warning display and extension click.

### Out of scope
- Full auth redesign.
- Multi-device session management.

## 4) User stories

- As a logged-in user, I want a warning before session expiration, so I can keep my work.

## 5) Acceptance criteria (Given / When / Then)

1. Given an active session with <2 minutes remaining,
   When user is active in app,
   Then warning modal is shown.

2. Given warning modal is visible,
   When user clicks "Extend session",
   Then frontend calls refresh endpoint and session is extended.

## 6) Edge cases

- Refresh endpoint fails due to expired token.
- User has multiple tabs open.

## 7) Technical notes

### Backend
- Add/confirm refresh endpoint behavior and expiry validation.

### Frontend
- Add session countdown hook + warning modal.

### API/Contract
- `POST /api/session/refresh` with predictable error responses.

## 8) Risks and dependencies

- Risk: race conditions with multi-tab refresh.
- Dependency: current auth/session implementation.
- Mitigation: idempotent refresh handling and shared tab sync strategy.

## 9) Rollout and rollback

- Rollout: enable behind feature flag for staged rollout.
- Rollback: disable flag and keep current behavior.
