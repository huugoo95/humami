# Spec 018 — Critical dependency updates

## Why
The September 23 audit of develop finds Next.js15.5.20 with critical advisories, critical findings in development tooling, Node18 container images past end of support, and a Spring Boot3.4.0 backend with old transitive components. Version age alone does not prove exploitability. This change separates confirmed advisory ranges, reachable features and support risks.

## Goal, priority and user story
Priority: Now/security. As the project owner, I want critical dependency fixes defined and completed without unrelated product changes. Success: identified critical/high findings in the affected dependency graphs are removed or explicitly assessed with residual applicability evidence; frontend and backend tests/builds and remote checks pass. Deadline: not provided.

## What Changes
- Update Next.js within the15.5 maintenance line and align its ESLint configuration; refresh vulnerable compatible lockfile dependencies without blanket major upgrades.
- Move frontend Docker/CI runtime from EOL Node18 to Node24 LTS.
- Update the Spring dependency baseline to a compatible patched release and address critical/high transitive findings; remove confirmed unused vulnerable dependencies instead of introducing unnecessary replacements.
- Refresh deprecated CI action runtimes and add frontend verification including dependency audit.
- Record exact before/after versions, dated primary sources, tests and residual risks in this change.

## Capabilities
### New Capabilities
- `dependency-security`: reviewable version remediation and executable frontend/backend validation.
### Modified Capabilities
None; existing API/pagination behavior is unchanged.

## Scope and non-goals
Includes package manifests/lockfile, Maven dependency management, Docker runtime version and relevant CI/tests/configuration adaptations. No business features, schema migration, publication, deployment, live-server modifications or branch-protection changes. Framework major migration only if a required fix cannot be obtained compatibly, with design evidence first. Production still originates from master in a separately requested release. User authorized both definition and implementation.
