## ADDED Requirements

### Requirement: Quality-first default catalogue order

The API SHALL order quality-eligible `GET /api/meals` results with a null,
blank or whitespace-only query by effective quality score descending before
pagination. It MUST use persisted ID ascending as a deterministic tie-breaker
for equal effective scores.

#### Scenario: Higher-quality recipe across a page boundary

- **GIVEN** eligible recipes with differing quality scores, including a recipe
  with score 0.97 that would otherwise fall after the first page by ID
- **WHEN** a blank-query request is made with a page size that spans the
  recipes across two pages
- **THEN** the 0.97 recipe SHALL precede every recipe with a lower score,
  regardless of persisted ID

#### Scenario: Equal quality scores

- **GIVEN** multiple eligible recipes with the same quality score
- **WHEN** identical blank-query requests are repeated
- **THEN** those recipes SHALL appear in persisted ID ascending order with no
  duplicates or omissions across pages

#### Scenario: Legacy unscored recipes at zero threshold

- **GIVEN** recipes with positive quality scores and recipes with missing or
  null quality scores
- **WHEN** a blank-query request uses `minQualityScore=0`
- **THEN** the unscored recipes SHALL be treated as score 0.0 and appear after
  positive-score recipes, ordered by persisted ID

### Requirement: Preserve existing catalogue and search contracts

The API SHALL preserve eligibility predicates, normalized pagination metadata,
response shape and the existing nonblank search relevance ordering.

#### Scenario: Default quality threshold

- **GIVEN** recipes with scores 0.5, 0.49 and no score
- **WHEN** a blank-query request omits `minQualityScore`
- **THEN** only the recipe with score 0.5 SHALL be eligible

#### Scenario: Nonblank search

- **GIVEN** matching eligible recipes with different fuzzy relevance scores
- **WHEN** a nonblank query is requested
- **THEN** results SHALL remain ordered by existing relevance descending, with
  persisted ID ascending only to break equal relevance scores
