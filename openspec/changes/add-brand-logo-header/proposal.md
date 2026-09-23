# Spec 020 — Brand logo v1 in the site header

## Why
The site header shows "Humami" as plain Inter text. `docs/HUMAMI.md` lists an initial visual identity (logo + minimal brand system) as a NOW priority, and `docs/BRAND.md` defines the logo deliverables. The owner has approved logo v1.

## Goal and priority
Priority: NOW (visual identity). As a visitor, I want to recognise the Humami brand in the header on every page.
Success metric: the approved reverse wordmark renders in the header on mobile and desktop, links to `/` and exposes the accessible name "Humami". Usage metrics are not applicable.

## What Changes
- Add the approved brand assets to `humami-web/public/brand/` (wordmark variants, symbol, favicon sources) as outlined SVG plus PNG/ICO exports.
- Replace the text link in `AppHeader` with the reverse wordmark (ivory "umami", soft-gold italic "h" and drop) on the existing `humami-accent-dark` header.
- Record the logo concept and usage rules in `docs/BRAND.md`.

## Non-goals
Favicon/app-icon metadata, footer, OG image, palette or typography changes. The favicon assets are added but not wired.

## Capabilities
### New Capabilities
- `brand-logo`: header brand mark.

### Modified Capabilities
None: no current OpenSpec baseline exists.

## Impact and constraints
Frontend only: one component and static assets. No new dependency, API, data or migration. Deadline: not provided.
