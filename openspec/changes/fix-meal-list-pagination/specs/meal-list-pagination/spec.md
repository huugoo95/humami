## ADDED Requirements

### Requirement: Filter eligibility before pagination
The API SHALL apply effective quality score >= minQualityScore before counting or paginating GET /api/meals results. Missing or null quality scores MUST have effective score 0.0. The default threshold SHALL remain 0.5.

#### Scenario: Mixed-quality catalogue
- **GIVEN** 25 eligible meals interleaved with 15 ineligible meals in an unchanged dataset
- **WHEN** the client requests all pages with limit 12 and no query
- **THEN** page sizes SHALL be 12, 12 and 1, with each eligible ID appearing exactly once and no ineligible IDs

#### Scenario: Threshold boundary and legacy records
- **GIVEN** records with score 0.5, score 0.49, absent quality and null score
- **WHEN** the default threshold is used
- **THEN** only the score 0.5 record SHALL qualify

#### Scenario: Zero threshold includes legacy records
- **GIVEN** records with absent quality or missing/null score
- **WHEN** minQualityScore is 0
- **THEN** those records SHALL qualify as score 0.0

### Requirement: Stable ordering
The API SHALL order blank-query results by persisted ID ascending and nonblank-query results by existing relevance descending then ID ascending, before pagination. Stability MUST hold for unchanged data.

#### Scenario: Repeated catalogue requests
- **GIVEN** an unchanged dataset
- **WHEN** identical blank or whitespace-only query requests are repeated
- **THEN** the same ordered IDs SHALL be returned, in ascending ID order

#### Scenario: Search ties across pages
- **GIVEN** eligible matching records with equal relevance spanning a page boundary
- **WHEN** consecutive search pages are requested
- **THEN** tied records SHALL be ordered by ID ascending without duplicates or omissions, preserving the existing relevance calculation

### Requirement: Accurate metadata and compatible boundaries
The API SHALL preserve its response fields and report totalItems for the complete eligible result set and totalPages as max(ceil(totalItems/normalizedLimit),1). It SHALL retain page>=1 and limit in 1..48 normalization.

#### Scenario: Count independent of page size
- **GIVEN** 25 eligible results and limit 12
- **WHEN** any page is requested with or without a search query
- **THEN** totalItems SHALL be 25 and totalPages SHALL be 3

#### Scenario: Empty result set
- **GIVEN** no eligible results
- **WHEN** page 1 is requested
- **THEN** items SHALL be empty, totalItems 0 and totalPages 1

#### Scenario: Out-of-range page
- **GIVEN** 25 eligible results and limit 12
- **WHEN** page 4 is requested
- **THEN** items SHALL be empty, page 4, totalItems 25 and totalPages 3

#### Scenario: Parameter normalization
- **GIVEN** an otherwise valid request
- **WHEN** page is below 1 or limit is outside 1..48
- **THEN** page SHALL be clamped to at least 1 and limit to 1..48 before computing results and metadata
