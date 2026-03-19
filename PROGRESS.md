# Project Progress

_Last updated: 2026-03-19_

## In progress
- Spec 012: persist About content in MongoDB + add protected `PATCH /api/about` update path.

## Blocked
- None.

## Done
- Spec 011 merged: `GET /api/about` endpoint with title/story/photoUrl/updatedAt contract.
- Spec 010 merged: backend endpoint coverage uplift for meal/blog controllers and write-auth paths.
- Established engineering workflow and git flow documentation.
- Added operating docs (`docs/HUMAMI.md`, `docs/BRAND.md`, `docs/DEV.md`, `docs/RECIPES.md`, `docs/CONTENT.md`, `docs/brief-template.md`).
- Drafted new specs:
  - `specs/002-recipe-create-auth-secret.md`
  - `specs/003-tests-and-ci-baseline.md`
  - `specs/004-simple-cd-safe-deploy.md`
  - `specs/005-blog-section-foundation.md`
  - `specs/006-seo-google-indexing-baseline.md`
  - `specs/007-about-page-foundation.md`

## Next steps
1. Review and approve specs 002–007.
2. Break implementation into feature branches from `develop`.
3. Deliver in this order: security -> tests/CI -> CD -> blog -> SEO -> about.
