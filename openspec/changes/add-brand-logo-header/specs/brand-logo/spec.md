## ADDED Requirements

### Requirement: Header brand mark
The site header SHALL display the approved reverse Humami wordmark as a link to the home page on every page, and the link MUST expose the accessible name "Humami".

#### Scenario: Any page on desktop
- **GIVEN** a visitor on any page at a desktop viewport
- **WHEN** the header renders
- **THEN** the reverse wordmark SHALL be visible on the dark header, linking to `/`, with alt text "Humami"

#### Scenario: Mobile viewport
- **GIVEN** a visitor at a 375 px wide viewport
- **WHEN** the header renders
- **THEN** the wordmark SHALL keep its aspect ratio, be at least 80 px wide and not overlap the menu button

#### Scenario: Brand fonts unavailable
- **GIVEN** web fonts fail to load
- **WHEN** the header renders
- **THEN** the wordmark SHALL render identically, because it is an outlined SVG
