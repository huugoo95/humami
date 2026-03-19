# Enforcement Matrix (Process vs Quality)

This document clarifies what can be guaranteed automatically and what still requires human review.

## Automated by branch protection (GitHub)
- No direct commit to protected branches
- PR required to merge
- Minimum required approvals
- Conversation resolution before merge
- No force-push to protected branches

## Semi-automated (CI checks)
- Build passes
- Tests pass
- Lint/format checks pass

> These depend on CI pipeline quality and coverage.

## Human-review only (cannot be fully guaranteed automatically)
- True TDD behavior quality (RED/GREEN/REFACTOR discipline)
- Architectural quality and trade-off correctness
- Product/UX adequacy
- Rollback realism and operational risk judgment

## Practical policy
1. Use branch protection to enforce merge flow.
2. Use CI checks to enforce objective quality gates.
3. Keep reviewer accountability (`huugoo95`) for non-automatable quality.
