# Workflow: Feature Delivery by Specification

## 0) Intake

Define:
- Problem
- User impact
- Goal and metric
- Priority
- Non-goals

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

## 7) Merge and follow-up

- squash merge preferred for feature branches
- verify in staging
- update progress docs if needed
