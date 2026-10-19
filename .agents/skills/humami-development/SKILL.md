---
name: humami-development
description: Implement or fix Humami code end to end, from branch to verified production. Excludes definition-only, editorial and content publication requests.
---

# Implement a Humami change

1. Understand the request. Product features need an agreed OpenSpec change ([feature spec](../humami-feature-spec/SKILL.md)); fixes and technical changes do not.
2. Create a short branch from updated `master` ([code delivery](../humami-code-delivery/SKILL.md)). Read [engineering rules](../../../ENGINEERING_RULES.md); consult architecture or library policy only when relevant.
3. Use RED → GREEN → REFACTOR for non-trivial logic. Run the affected module's tests, lint and build with its real configuration. The backend needs JDK 21 (Lombok fails on newer JDKs). Investigate failures; never treat broken tooling as passing.
4. Check the change locally when it is user-visible (`npm run dev` in `humami-web`, optionally against the production API).
5. For high-risk changes (see engineering rules) get an [independent review](../humami-code-review/SKILL.md) and fix blocking findings.
6. Deliver: PR to `master`, wait for green CI, squash merge. Merging deploys to production automatically.
7. Follow the deployment and [verify production](../humami-release/SKILL.md) for the affected behaviour. If the deploy rolled back or verification fails, fix forward or revert, and record the incident.

Ask the owner before anything listed under "Always ask first" in [AGENTS.md](../../../AGENTS.md). Report what is merged, deployed and verified, and anything not verified.
