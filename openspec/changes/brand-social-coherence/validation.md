# Validation — brand coherence and social sharing previews

Candidate: e22f95a on feat/023-brand-social-coherence.

## Implementation evidence

- The retired social card was replaced with the versioned JPEG
  og-humami-v2.jpg, verified as 1200×630 and visually checked against the
  approved wordmark and palette.
- Root and catalogue metadata use the current fallback card. The catalogue
  renders its own canonical URL and Open Graph URL; recipe metadata preserves
  the recipe URL and uses the recipe image when present.
- The root metadata declares branded browser and Apple icons. The App Router
  manifest declares the approved symbol icons, Spanish name, dark-burgundy
  theme and cream background.
- Playfair Display and Inter load through next/font. The global Arial and
  automatic dark-mode overrides are removed. Recipe detail and blog templates
  use semantic brand tokens; no undefined burgundy utility remains.

## Verification evidence

- RED: the new brandMetadata test initially failed because the module did not
  exist.
- GREEN: npm test passes 2 suites and 5 tests.
- npm run lint exits successfully. It reports the existing no-img-element
  warning in the unrelated our-story page.
- npm run build passes, including TypeScript validation and static generation.
  Next reports the pre-existing multiple-lockfile warning.
- Rendered local catalogue HTML exposes canonical
  https://humami.es/meals, matching og:url, the new image URL, 1200×630 image
  dimensions, Twitter summary_large_image, manifest and icons.
- Playwright at a 375 px viewport reports innerWidth, viewport width and
  document scroll width all equal to 375, with no horizontal overflow.
- Independent review found no blocking issue. Its minor fallback-alt finding
  was corrected to include the recipe name and Humami, then tests and build
  were repeated successfully.
- openspec validate is unavailable because the openspec CLI is not installed in
  this environment. The artifact structure and requirement/scenario markers
  were checked manually.

## Rollback

Revert e22f95a to restore the prior social card, typography and public-page
styling. No API, data or infrastructure rollback is required.
