# Approved Libraries Policy

Use this as guidance to avoid dependency sprawl.

## Rule of thumb

- Prefer existing dependencies already used in project modules.
- Add new dependencies only when there is clear value.
- Document rationale in PR (problem, alternatives, trade-offs).

## Backend

- Reuse Spring Boot ecosystem where possible.
- Avoid adding overlapping libraries for validation, HTTP, or serialization unless required.

## Frontend

- Reuse existing state/data/UI stack already present in `humami-web`.
- Avoid introducing a second state management or data-fetching paradigm without RFC.

## Introducing a new library

PR must include:
1. Why current stack is insufficient
2. Candidate alternatives considered
3. Bundle/runtime/security impact
4. Migration plan (if replacement)
