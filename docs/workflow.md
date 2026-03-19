# Workflow: Feature Delivery by Specification

## 0) Intake

Define:
- Problem
- User impact
- Goal and metric
- Priority
- Non-goals
- Primary track (`BE/API`, `FE/UX`, `Infra/Deploy`, `Data/Recipes`, `SEO/Growth`)

Before execution, write the minimum task line:
`[TRACK] objective | done-criteria | deadline`

## 1) Functional spec

Create `specs/XXX-feature-name.md` from template.

Minimum required:
- user story
- scope in/out
- acceptance criteria (Given/When/Then)
- edge cases
- risks/dependencies

## 2) Technical design

Document implementation approach in the same spec or a linked design note:
- backend impact
- frontend impact
- API/data contract
- migration/security concerns

## 3) Task breakdown

Split into executable tasks:
- BE tasks
- FE tasks
- QA/docs tasks

Use same feature ID in branch names and PR.

## 4) Implementation

- Apply TDD for non-trivial logic.
- Keep commits focused and intention-based.
- Keep backend/frontend aligned through an explicit contract.

## 5) Validation

Before PR:
- tests pass
- lint/format pass
- acceptance criteria checked

## 6) Pull request

PR must include:
- spec ID and link
- summary of backend/frontend changes
- test evidence
- risks and rollback
- completed checklist

Branch targets:
- `feat/*` and `fix/*` -> `develop`
- `release/*` -> `master`, then back-merge to `develop`
- `hotfix/*` -> `master`, then back-merge to `develop`

## 7) Merge and follow-up

- squash merge preferred for feature branches
- verify in staging
- update progress docs if needed
- if operational learning happened, update `ops/` runbooks and logs (`ops/decisions.md`, `ops/incidents.md`)

## Operational memory

For recurring execution knowledge (deploy, recipes ops, SEO cycles), use:
- `ops/README.md`
- `ops/runbook-deploy.md`
- `ops/runbook-recipes.md`
- `ops/runbook-seo.md`
