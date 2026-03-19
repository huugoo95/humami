# Spec 013: about-image-upload-flow

- **Status:** Draft
- **Owner:** Hugo + tenacitas
- **Created:** 2026-03-19

## 1) Problem
`/api/about` currently accepts `photoUrl` as text. Desired flow is equivalent to meal images: upload binary image, store in S3, and return a usable URL.

## 2) Goal and metric
- Goal: enable `PUT /api/about/image` multipart upload and keep `GET /api/about` returning a usable `photoUrl`.
- Success metric: after image upload, `GET /api/about` includes signed/usable image URL.

## 3) Scope
- Add `PUT /api/about/image` with multipart field `image`.
- Persist S3 key in About document.
- Resolve key to usable URL in response.
- Keep write auth protection for `/api/about/**`.

## 4) Acceptance criteria
1. Given authenticated request to `PUT /api/about/image` with valid file,
   when upload succeeds,
   then About is updated and response includes usable `photoUrl`.
2. Given request without write secret,
   then endpoint is rejected by write auth.
3. Given stored `photoUrl` as S3 key,
   then `GET /api/about` returns a signed/usable URL.
