## 1. Backend domain and protected assets

- [ ] 1.1 Add guide, guide-lead and guide-access-token domain/persistence
  models, repositories, DTOs and MapStruct mappings with unique indexes and
  timestamp handling.
- [ ] 1.2 Write failing backend tests for publication visibility, lead
  deduplication, privacy/marketing fields and token expiry/revocation; then
  implement the minimal service layer to pass them.
- [ ] 1.3 Extend storage through a dedicated private-PDF upload and short-lived
  document-URL capability; validate type/size and keep image normalization
  unchanged.
- [ ] 1.4 Add authenticated guide CRUD/publish/cover/document endpoints under
  the existing write-auth convention, with upload and authorization tests.

## 2. Public access and delivery

- [ ] 2.1 Add public guide discovery/detail, access-request, token-redemption
  and authorized document endpoints.
- [ ] 2.2 Implement guide-scoped secure browser access sessions, hashed opaque
  credentials, generic repeat responses, input validation, rate limits and
  honeypot control; start with failing tests for each denial case.
- [ ] 2.3 Add the `GuideAccessEmailSender` application port, configuration
  validation and a production provider adapter; use a fake adapter in tests.
- [ ] 2.4 Configure production sender domain, provider credentials, privacy
  notice URL/version and private object storage; verify delivery to a
  controlled address without recording credentials in the repository.

## 3. Frontend discovery and reader

- [ ] 3.1 Add guide data-access modules/types, `/guias` index and public
  `/guias/[slug]` landing template with empty, unavailable and published
  states.
- [ ] 3.2 Add the access form with privacy acknowledgement, optional marketing
  opt-in, validation, abuse-safe responses and immediate reader transition.
- [ ] 3.3 Add `/guias/[slug]/leer` with authorized inline PDF view, download
  fallback, expired-access state and return-link redemption flow.
- [ ] 3.4 Add Guides to navigation, guide metadata and sitemap support; test
  the public metadata contract.

## 4. First guide, validation and delivery

- [ ] 4.1 Publish the final pizza guide through the authenticated guide flow;
  upload its final cover and PDF to private storage rather than committing the
  document to `public/`.
- [ ] 4.2 Run backend tests, frontend lint/tests/build and focused API tests;
  record RED → GREEN evidence for the non-trivial behaviours.
- [ ] 4.3 Perform desktop and 375 px visual review for catalogue, landing,
  form, immediate reader and email-return reader. Verify sitemap and rendered
  guide metadata.
- [ ] 4.4 Obtain independent review, resolve blockers, deliver in a PR to
  `develop`, and record validation/delivery evidence before archiving.
