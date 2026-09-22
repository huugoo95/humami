# Validation — 2026-09-22

## Implemented scope

Eight project skills, a 31-line root AGENTS.md, six specialist profiles and a delegation index. Procedures moved from old workflow/runbook pages to skills; compatibility pages point to their canonical locations. Shared knowledge and executable scripts remain separate. No application source, runtime scripts, dependencies or remote protection configuration changed.

## Checks executed

- Official skill-creator quick_validate.py: all eight skills pass. PyYAML used from a temporary directory, not added as a project dependency.
- OpenSpec strict validation: define-humami-ai-workflow is valid.
- Local Markdown references in changed/new files resolve; git diff --check passes.
- Actual Codex discovery: all eight humami skills appeared in this session's available-skills catalog, rooted at the repository .agents/skills directory.
- Isolated one-step recipe fixture: existing validator exits 0, confirming the documented gap. Authoring requires an additional meaningful-step check; the validator was not changed.
- No builds or application tests: this change only modifies instructions/documentation. No runtime behavior is claimed as tested.

## Independent evaluations

A read-only scenario evaluator traced eight requests through the actual skills: saved Spanish blog draft; definition-only OpenSpec; UI fix with missing checks; one-step recipe; known meal ID with failed image; timed-out creation with unknown ID; develop-only production candidate; parallel editing specialists. It found no blocking routing or stopping-condition contradiction. This was a reasoning-based evaluation against concrete instructions, not live publication/deployment testing.

An independent reviewer inspected the tracked and new instruction files against all four capability specs, actual API contracts and release scripts. It found missing release prerequisites during the initial pass; release step 3 now restores incident, Docker/Compose, registry, runtime configuration and TLS preconditions. The completed second pass reported no remaining actionable findings. Review was performed on the working tree; the delivered commit must match that reviewed implementation. Subsequent changes are limited to progress/evidence records unless review is refreshed.

## Limitations and delivery

No production writes, deployments, builds, staging assumptions or remote protection changes. Missing required checks block autonomous integration. GitHub CLI is not authenticated in the current environment; branch push/PR status will be recorded after the delivery attempt. Archive remains pending until implementation acceptance and delivery requirements are satisfied.
