# Spec 023 — Brand coherence and social sharing previews

## Why

Humami's header uses Logo v1, but shares of the recipe catalogue still use
the retired all-caps social image. The catalogue inherits the home page's Open
Graph URL, so a shared meals link identifies itself as the home page. The
recipe detail also references undefined Tailwind burgundy utilities and
visibly falls back to unbranded styling. Blog pages retain generic typography
and grayscale styles.

This breaks visual continuity between the site, browser/app icons and shared
links at the first point people encounter the brand.

## Goal and priority

Priority: NOW — complete the minimum visual identity system and make shared
links recognisable as Humami.

As a visitor or recipient of a shared Humami link, I want to see the current
Humami identity consistently in the preview and on the destination page.

Success metric: a fresh share of the catalogue exposes a page-specific
canonical URL and a 1200×630 current-brand image; key public surfaces use the
same loaded heading/body typefaces and semantic brand tokens. Social-network
caching is external, so verification relies on rendered metadata and a new
image URL rather than immediate third-party cache refresh.

## User story

As someone receiving a Humami link in a messaging app, I can recognise the
brand and understand whether the link is the recipe catalogue or an individual
recipe before opening it.

## Scope

- Replace the global fallback social image with a versioned 1200×630 Humami
  brand asset that uses the approved wordmark without recreating the logo.
- Define explicit Open Graph, Twitter and canonical metadata for the catalogue.
- Retain per-recipe metadata and make its fallback image the current asset.
- Wire approved favicon, Apple touch icon and web-app manifest assets through
  Next metadata.
- Make the approved heading font a deterministic web font, retain Inter for
  body/interface text, and remove conflicting global CSS rules.
- Consolidate the active semantic palette around approved burgundy, dark
  burgundy, gold, cream and ink values.
- Bring the public recipe detail and blog listing/article templates into that
  system, including fixing undefined recipe-detail color utilities.
- Apply modest footer brand reinforcement using an approved asset only if it
  remains legible and does not duplicate navigation.

## Non-goals

- No redesign or alteration of the approved logo files.
- No AI-generated logo, food photography or editorial copy.
- No new CMS/API endpoint, database migration, user setting, dark-mode
  feature, analytics event or dependency.
- No guarantee that third-party messaging applications immediately invalidate
  their own cached cards.
- No change to recipe ranking, recipe content or authentication.

## Risks and dependencies

The new social image must be publicly retrievable at a stable absolute URL;
using a new filename prevents stale-card confusion but external caches may
still require their normal refresh/debugger flow. Individual recipes may
supply remote images; those URLs must remain usable by social crawlers or
fall back safely to the branded image. Typography changes can alter line wraps
and require desktop and mobile visual review. Existing static brand files in
public/brand are the required source of truth.

The work is frontend-only. It requires no backend contract, data migration or
security change.

## Delivery

Implement on feat/023-brand-social-coherence, validate in a PR to develop,
and do not deploy production as part of this change.
