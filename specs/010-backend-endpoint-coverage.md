# Spec 010: backend-endpoint-coverage

- **Status:** In Progress
- **Owner:** tenacitas
- **Created:** 2026-03-19

## 1) Problem

Backend HTTP endpoint coverage is low and currently under-validates request routing/auth behavior for meal and blog controllers.

## 2) Goal and metric

- Goal: increase meaningful automated coverage for backend HTTP endpoints.
- Success metric: add controller tests that validate happy paths and key auth/error paths for `/api/meals/**` and `/api/blog/**`, and raise backend coverage measurably toward 90%.

## 3) Scope

### In scope
- WebMvc tests for `MealController` and `BlogPostController`.
- Auth interceptor behavior coverage for protected methods with/without write secret.
- Coverage reporting configuration for backend module via JaCoCo.

### Out of scope
- Functional behavior changes in production controllers/services.
- New endpoint features.
- Full 90% guarantee in one pass if blocked by environment/runtime constraints.

## 4) User stories

- As a maintainer, I want endpoint tests to catch regressions in HTTP contract and write-auth enforcement.

## 5) Acceptance criteria (Given / When / Then)

1. Given read endpoints in meals/blog,
   When requests are made with expected params,
   Then status and body mapping are verified and service calls are asserted.

2. Given protected write endpoints,
   When requests are made without valid `X-HUMAMI-SECRET`,
   Then requests are rejected and services are not called.

3. Given write secret is not configured,
   When protected endpoints are hit,
   Then API returns `503 Service Unavailable`.

4. Given tests run with coverage,
   When the backend test suite executes,
   Then a coverage report is generated for visibility.

## 6) Edge cases

- Multipart endpoints for meal image and blog cover update.
- Default paging params in `/api/meals` list endpoint.

## 7) Technical notes

### Backend
- Add focused `@WebMvcTest` suites for controllers.
- Keep business logic mocked at service layer.

### Frontend
- No changes.

### API/Contract
- Assert existing response codes and payload fields only.

## 8) Risks and dependencies

- Risk: local environment may miss Java runtime needed for Maven execution.
- Dependency: Java/JDK + Maven wrapper execution.
- Mitigation: document exact run command + blocker and keep test changes isolated.

## 9) Rollout and rollback

- Rollout: merge test-only PR into `develop`.
- Rollback: revert PR commit if tests prove unstable.
