# Spec 009: ops-knowledge-structure

- **Status:** Draft
- **Owner:** Hugo + tenacitas
- **Created:** 2026-03-19

## 1) Problem
Operational knowledge (deploy, recipe publishing, SEO routines, incident handling) gets mixed into chat and generic docs, creating context loss and execution inconsistency.

## 2) Goal and metric
- Goal: separate product/engineering docs from operational runbooks and make task execution format consistent.
- Success metric:
  - runbooks exist for deploy/recipes/SEO
  - every meaningful task uses `[TRACK] objective | done-criteria | deadline`
  - incidents/decisions are logged in dedicated files

## 3) Scope

### In scope
- Add `ops/` directory with runbooks and operational logs.
- Define track taxonomy for task classification.
- Add quick-start and closure checklist for daily execution.
- Clarify boundary between `docs/` and `ops/`.

### Out of scope
- CI/CD automation changes.
- Runtime code changes in backend/frontend.
- Process enforcement tooling (lint/CI rule checks).

## 4) User stories
- As an owner, I want repeatable operational instructions, so that work does not depend on prior chat context.
- As an operator, I want a short intake format, so that tasks stay focused and verifiable.

## 5) Acceptance criteria (Given / When / Then)
1. Given a new task
   When execution starts
   Then the task can be expressed with `[TRACK] objective | done-criteria | deadline`.

2. Given deploy/recipe/SEO recurring work
   When operator needs instructions
   Then runbooks exist under `ops/`.

3. Given a process/documentation question
   When deciding where to store content
   Then boundary between `docs/` and `ops/` is explicit and documented.

4. Given operational incidents or durable decisions
   When work completes
   Then they can be captured in `ops/incidents.md` or `ops/decisions.md`.

## 6) Edge cases
- Tasks touching multiple tracks: choose one primary track and list dependencies.
- Over-documentation risk: keep templates short and pragmatic.

## 7) Technical notes
- Documentation-only change set.
- No application runtime impact.

## 8) Risks and dependencies
- Risk: process overhead if templates are too heavy.
- Mitigation: one-line intake + short checklists.

## 9) Rollout and rollback
- Rollout: merge docs changes and apply immediately for new tasks.
- Rollback: remove/trim `ops/` files if process becomes noisy.
