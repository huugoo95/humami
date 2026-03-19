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

## 2026-03-19
- Decision: Default reviewer is `huugoo95` (Hugo) across all tracks.
- Scope/Track: Cross-track
- Rationale: Single accountable human reviewer simplifies quality gate and decision latency.
- Impact: PR flow has one explicit reviewer by default.
- Revisit trigger: Team grows or review load becomes a bottleneck.

## 2026-03-19
- Decision: Docs-only PRs do not require a spec, unless they alter product behavior, API contract, or release policy.
- Scope/Track: Cross-track
- Rationale: Avoid process overhead for pure documentation maintenance while protecting behavior-affecting changes.
- Impact: Faster docs iteration with clear exception boundaries.
- Revisit trigger: Repeated docs PRs cause behavior drift without specs.

## Pending decisions
- Release gate: should deploy to production require `master` only, or is `develop` deployment acceptable by policy?
- Incident severity: define P1/P2/P3 levels and mandatory response checklist.
