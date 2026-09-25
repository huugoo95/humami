# Design

## Current state

The root layout declares og-default.jpg for Open Graph and Twitter, including
the home URL and root canonical. The catalogue has no page metadata, so it
inherits all of those values. The current image is a 1200×630 legacy all-caps
lockup.

Recipe pages generate individual metadata, but their fallback is also the
legacy image. AppHeader correctly renders the approved reverse wordmark. The
application imports Inter, but Tailwind declares Playfair Display without
loading it; global CSS forces Arial and defines unrelated automatic dark-mode
variables. Recipe detail uses unconfigured burgundy utilities. Blog pages use
generic black and gray utilities.

## Brand-system decision

1. Use the approved Logo v1 static assets as-is. The social asset shall use
   the approved wordmark rather than typesetting a substitute. Create one new
   versioned static social image at 1200×630 with the approved burgundy, gold,
   cream and ink palette. Remove the retired image only after references move.
2. Keep metadataBase as https://humami.es. Root metadata uses the new image.
   The catalogue exports static metadata with its own canonical and Open Graph
   URL; individual recipe metadata retains its canonical URL and falls back to
   the new image.
3. Provide image width, height, type and descriptive alt text in Open Graph
   metadata. Twitter continues to use summary_large_image.
4. Declare existing favicon, Apple icon and web-app icon assets through Next
   metadata. Add an App Router manifest with brand symbol assets, Spanish app
   name, theme color #5F1E30, and background color #F7F4EE.
5. Load Playfair Display and Inter through next/font/google, expose them as
   Tailwind-backed variables or classes, and remove global rules that force
   Arial or an unsolicited dark scheme. Heading roles use Playfair; body,
   controls and metadata use Inter.
6. Make humami semantic colors the active public-page palette: burgundy
   #7B2640, dark burgundy #5F1E30, gold #D4AF7F, cream #F7F4EE, and readable
   ink. Replace undefined recipe-detail utilities with semantic tokens.
7. Recipe detail and blog templates share the existing shell and its spacing,
   heading, surface, border and link treatment. Their content hierarchy stays
   intact; this is visual alignment, not content restructuring.

## Accessibility and responsive behavior

- The social image is informational metadata; its alt text identifies Humami
  and, for recipe pages, the recipe name.
- Text colours must retain readable contrast on cream/white and dark-burgundy
  surfaces.
- At 375 px and desktop widths, header, titles, cards, image areas and footer
  must not overflow or overlap.
- Browser icons and manifest use the symbol rather than the wordmark where
  small sizes make the wordmark illegible.

## Verification and rollback

Run frontend lint, tests and production build. Add or update focused tests for
metadata/page contracts where the current test setup supports them. Inspect
rendered HTML metadata for home, catalogue, and one recipe page, and confirm
the referenced static social image returns successfully at 1200×630. Perform
desktop and 375 px visual checks for home, catalogue, recipe detail, blog list
and blog article. Reverting the implementation commit restores the previous
visual system and social image; no data rollback is required.
