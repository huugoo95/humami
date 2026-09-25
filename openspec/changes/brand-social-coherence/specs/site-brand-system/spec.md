## ADDED Requirements

### Requirement: Deterministic brand typography

Public Humami pages SHALL use loaded Playfair Display for heading roles and
loaded Inter for body and interface roles. Global styles MUST NOT override
these typefaces with Arial or activate an unrelated automatic dark scheme.

#### Scenario: Render a public page

- **GIVEN** a visitor loads a public Humami page
- **WHEN** its heading and body text render
- **THEN** headings SHALL use the configured Playfair Display font family
- **AND THEN** body and interface text SHALL use the configured Inter font
- **AND THEN** the page SHALL retain the light brand surface when the device
  prefers a dark colour scheme

### Requirement: Semantic brand palette is complete

Public page components SHALL use approved semantic brand tokens for burgundy,
dark burgundy, gold, cream and ink. No public component MAY rely on an
undefined Tailwind colour utility.

#### Scenario: Render a recipe detail

- **GIVEN** a visitor opens a recipe detail page
- **WHEN** headings, metadata chips, ingredient cards and method cards render
- **THEN** their colour and border utilities SHALL resolve to configured
  semantic brand tokens
- **AND THEN** they SHALL remain readable on their assigned surfaces

### Requirement: Public editorial surfaces are visually coherent

The recipe detail, blog listing and blog article SHALL use the shared Humami
type hierarchy, palette and surface treatment while preserving existing
content and routes.

#### Scenario: Browse at mobile and desktop widths

- **GIVEN** a visitor views each public editorial surface at 375 px and desktop
  widths
- **WHEN** the page renders
- **THEN** titles, body text, cards, links and footer SHALL match the approved
  brand system
- **AND THEN** content SHALL not overflow, overlap or become obscured
