# Architecture Guidelines

## Monorepo layout

- `humami-backend`: API, business logic, persistence
- `humami-web`: web UI

## Backend boundaries

- Controller layer: transport + request/response mapping
- Service/Application layer: use-case orchestration
- Domain layer: business rules and core models
- Repository layer: persistence access

Avoid:
- business logic in controllers
- DB concerns leaking into domain logic

## Frontend boundaries

- Feature-first organization (by domain/use-case)
- Data access isolated in API modules/hooks
- Presentational components separated from data orchestration

Avoid:
- API calls scattered inside presentational components
- global mutable state for feature-local concerns

## API contract discipline

- Define request/response contract clearly before FE+BE integration.
- Keep error shapes predictable.
- Version or feature-flag breaking changes.
