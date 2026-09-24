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
- Decision (historical; superseded by 2026-09-22): Default reviewer is `huugoo95` (Hugo) across all tracks.
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
- Incident severity: define P1/P2/P3 levels and mandatory response checklist.

## Pending action
- Deliver CI/protection configuration in a separate change; inspect actual GitHub rules before enabling autonomous integration. A historical protection update returned 404; this is not evidence of current permissions. See [follow-ups](workflow-followups.md).

## 2026-09-22 — AI workflow (spec 016)
- Decision: Use a minimal AGENTS.md, selectively loaded skills and temporary specialists coordinated within the current task. Migrate procedures; retain shared knowledge and executable scripts in their existing roles.
- Decision: Definitions use OpenSpec; historical specs stay available through compatibility indexes.
- Decision: Implementation normally targets develop through PR, independent AI review and required green checks. This supersedes the historical universal Hugo-review policy, not effective GitHub protections; missing prerequisites block integration.
- Decision: Production releases originate exclusively from master with a concrete version and explicit deployment request. Develop is integration only.
- Scope: Instructions/procedures; no CI or remote protections configured by this change, no LinkedIn or permanent orchestration service.
- Revisit trigger: Repeated routing failures, new channels or team/release requirements.

## 2026-09-24 — Isolated development hosting (spec 019)
- Decision: Run dev.humami.es on the existing ARM64 host, with its own Mongo volume, database credentials and write secret; do not copy production data.
- Decision: Build dev images off-host from develop, tag by commit and pin deployed digests. Publication is automatic; deployment remains explicit. Production remains master-only.
- Decision: Until independent object storage exists, dev uses no real S3 credentials and the proxy returns503 for image uploads.
- Scope: Resource-capped dev containers and additive TLS/proxy routing. Existing production application containers and rollback images are retained.
- Evidence and runtime details: openspec/changes/provision-isolated-dev-environment/validation.md.
