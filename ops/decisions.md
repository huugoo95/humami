# Operational Decisions

Record decisions that should persist beyond chat history.

## Template
- Date:
- Decision:
- Scope/Track:
- Rationale:
- Impact:
- Revisit trigger:

---

## 2026-03-19
- Decision: Introduce track-based operational structure and ops runbooks.
- Scope/Track: Cross-track (BE/API, FE/UX, Infra/Deploy, Data/Recipes, SEO/Growth)
- Rationale: Reduce context loss and avoid re-explaining process each session.
- Impact: Faster onboarding and more consistent execution.
- Revisit trigger: If process overhead becomes too high or tracks need splitting.

## Pending decisions
- Track ownership model: who is default reviewer/owner per track?
- Spec strictness: do docs-only PRs always require a spec, or allow a lightweight exception label?
- Release gate: should deploy to production require `master` only, or is `develop` deployment acceptable by policy?
- Incident severity: define P1/P2/P3 levels and mandatory response checklist.
