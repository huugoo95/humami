## ADDED Requirements

### Requirement: Current branded fallback social card

The site SHALL publish a versioned, publicly retrievable 1200×630 fallback
social image that uses the approved Humami Logo v1 assets and current brand
palette. It MUST replace the retired fallback image in root and recipe
fallback metadata.

#### Scenario: Share a page without a page-specific photo

- **GIVEN** a crawler reads a Humami page whose metadata uses the fallback image
- **WHEN** it resolves the Open Graph image URL
- **THEN** the URL SHALL identify the new versioned brand asset and SHALL
  return a 1200×630 image using the approved Humami identity

### Requirement: Catalogue metadata identifies the catalogue

The catalogue page SHALL expose its own canonical, Open Graph and Twitter
metadata instead of inheriting the root URL.

#### Scenario: Share the recipe catalogue

- **GIVEN** a messaging crawler requests https://humami.es/meals
- **WHEN** it reads the document metadata
- **THEN** the canonical URL and og:url SHALL be https://humami.es/meals
- **AND THEN** the Open Graph and Twitter images SHALL reference the current
  fallback social card
- **AND THEN** Open Graph SHALL identify the image dimensions and descriptive
  alt text

### Requirement: Individual recipe sharing remains specific

An individual recipe page SHALL continue to expose its own title, description
and canonical URL. It MUST use the recipe image when suitable and the current
branded fallback when no recipe image is available.

#### Scenario: Share a recipe with an image

- **GIVEN** a recipe has a usable public image URL
- **WHEN** its page metadata is generated
- **THEN** the Open Graph and Twitter image SHALL reference that recipe image
- **AND THEN** its canonical URL and og:url SHALL identify that recipe page

#### Scenario: Share a recipe without an image

- **GIVEN** a recipe has no usable image URL
- **WHEN** its page metadata is generated
- **THEN** the Open Graph and Twitter image SHALL reference the current
  branded fallback social card

### Requirement: Browser and installed-app identity

The site SHALL declare approved small-format brand assets for browser, Apple
touch and web-app contexts.

#### Scenario: Load site metadata

- **GIVEN** a browser loads any Humami page
- **WHEN** it reads root metadata and manifest
- **THEN** it SHALL find a branded favicon, Apple touch icon and web-app
  manifest
- **AND THEN** the manifest SHALL identify Humami in Spanish and use the
  approved theme and background colours
