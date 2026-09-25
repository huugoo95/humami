## 1. Social identity and metadata

- [x] 1.1 Produce and add a versioned 1200×630 static social card using the
  approved brand assets; remove the retired asset only after references move.
- [x] 1.2 Update root, catalogue and individual recipe metadata with correct
  canonical URLs, current fallback image data and descriptive alt text.
- [x] 1.3 Wire favicon, Apple touch icon and an App Router web-app manifest to
  approved assets and palette values.

## 2. Typography and visual system

- [x] 2.1 Load Playfair Display and Inter deterministically; remove conflicting
  global font and automatic-dark-scheme rules.
- [x] 2.2 Consolidate semantic brand tokens and replace undefined
  recipe-detail colour utilities.
- [x] 2.3 Align recipe detail, blog listing, blog article and footer with the
  shared brand hierarchy without changing their content contracts.

## 3. Verification and delivery

- [x] 3.1 Add/update focused metadata tests where supported, then run frontend
  lint, tests and production build.
- [x] 3.2 Verify rendered metadata for home, catalogue, and a recipe; verify
  the social image dimensions and public response.
- [x] 3.3 Perform desktop and 375 px visual review for home, catalogue, recipe
  detail, blog list and blog article.
- [x] 3.4 Obtain independent review and resolve blocking findings.
- [ ] 3.5 Deliver through a PR to develop; do not deploy production.
- [ ] 3.6 Archive after acceptance.
