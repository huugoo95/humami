# image-delivery

## ADDED Requirements

### Requirement: Optimized images are cached

The frontend runtime SHALL persist optimized images in its cache and serve repeated requests from it, with a browser cache lifetime of at least 30 days.

#### Scenario: Repeated image request

- **WHEN** the same optimized image URL is requested twice from the production runtime image
- **THEN** the second response is a cache HIT and the logs contain no cache write errors

### Requirement: Responsive image sizing

Remote images SHALL declare `sizes` that match their rendered layout, and above-the-fold images SHALL load with priority.

#### Scenario: Meal card on a high-density screen

- **WHEN** a 2x screen renders the meal grid
- **THEN** the browser selects a variant proportional to the card width, not the 1920 px variant
