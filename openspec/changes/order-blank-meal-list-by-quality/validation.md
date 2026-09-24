# Validation — quality-first recipe catalogue ordering

Candidate: `0c8565c` on `feat/022-order-meals-by-quality`.

## Implementation evidence

- Blank and whitespace-only list queries create a database pageable sorted by
  `quality.score` descending and persisted ID ascending.
- Nonblank search retains its relevance-first ranking.
- Unit coverage asserts the requested pageable. MongoDB-backed coverage checks
  high scores before lower scores across pages, ID tie-breaking and the
  effective-zero position of missing/null scores at a zero threshold.

## Verification evidence

- `openspec validate order-blank-meal-list-by-quality --strict`: passed.
- Java 21 `mvn -DskipTests package`: passed locally; it compiles main and test
  sources.
- GitHub Actions Backend CI run 34 for `0c8565c`: passed with MongoDB
  integration tests enabled.
- GitHub Actions Frontend CI run 29 for `0c8565c`: passed.
- Local Surefire execution is unavailable on this macOS because Mockito cannot
  attach its Byte Buddy agent; the CI run supplies the executable test evidence.
- Independent review of `ebe2d94`, `c7ab219` and `0c8565c`: no actionable
  findings after adding effective-zero ordering coverage.

## Rollback

Revert the implementation commit to restore ID-first blank-query ordering. No
database or API rollback is required.
