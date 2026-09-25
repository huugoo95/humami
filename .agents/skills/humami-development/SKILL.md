---
name: humami-development
description: Implement or fix Humami code from an agreed specification using project engineering rules. Excludes definition-only, editorial and deployment requests.
---

# Implement a Humami change

1. Identify the agreed OpenSpec change (or an existing historical spec), scope and acceptance criteria. If missing, use [feature spec](../humami-feature-spec/SKILL.md); an explicit end-to-end implementation request allows necessary planning, but a definition-only request does not authorize code.
2. Read [engineering rules](../../../ENGINEERING_RULES.md). Apply [code delivery](../humami-code-delivery/SKILL.md) for working-branch setup before edits; retain an existing valid task branch. Do not create another user-owned task to do this work.
3. Read [architecture](../../../docs/architecture.md) only for architectural changes and [library policy](../../../docs/approved-libraries.md) when considering dependencies. For frontend consult its package/config and affected feature code; for backend consult pom.xml, controllers, DTOs and tests relevant to the change. Do not load content or production runbooks by default.
4. Agree the API/data contract before cross-stack implementation. For independent parallel work use the [delegation index](../../../ops/agents/README.md); the coordinator owns integration.
5. Use RED → GREEN → REFACTOR for non-trivial code. Keep evidence of the regression first, then the relevant tests. Documentation/instruction changes use reference, structure and behavioral scenario checks instead of artificial unit tests.
6. Run relevant tests, lint and build from the affected module using its actual configuration. Investigate failing commands; do not treat unavailable infrastructure or broken scripts as passing checks. Do not modify unrelated tooling to make a report green.
7. Check acceptance criteria and update changed knowledge. Request [independent review](../humami-code-review/SKILL.md), address findings and use [code delivery](../humami-code-delivery/SKILL.md) to complete the requested handoff.

Normally implementation ends integrated into develop through the delivery gates, not just with local edits. Respect a narrower user scope. If gates/permissions block completion, leave the deliverable pending and report the exact blocker; never include production deployment implicitly.
