---
name: humami-code-review
description: Independently review a Humami diff against its specification and executable evidence before integration. Produces findings; does not implement or merge.
---

# Review a candidate

Receive the change/spec, base and candidate commit (or an explicit working-tree diff), affected scope and evidence. Read [engineering rules](../../../ENGINEERING_RULES.md) and only affected contracts and code. The reviewer must be separate from the implementing agent for code integration.

- Verify acceptance criteria, boundary/error cases, regressions, auth/data impact and realistic rollback.
- Check that claimed tests/checks actually ran for the candidate and cover behavior. Read scripts or run non-mutating relevant checks if needed.
- For skills/instructions, exercise realistic requests and trace selected references and actions; do not validate by heading matches alone.
- Return actionable findings with severity, location, reproduction/reason and candidate identifier. Explicitly state residual risks and checks not performed; no findings is not proof of correctness.
- Do not edit the implementation, submit a human approval, push or merge as part of a review request.

The coordinator resolves findings and records review evidence. If the diff changes, review affected changes again and refresh checks on the final candidate. A working-tree review must be matched against the eventual commit before integration. Effective GitHub approvals remain distinct from this review.
