# Process versus enforcement

| Requirement | Source of enforcement | Current handling |
| --- | --- | --- |
| Working branches and PRs | Effective GitHub branch protections | Inspect remotely; documentation alone proves nothing |
| Tests/lint/build | Configured required CI checks on candidate | No workflows existed in the inspected base; missing gates block autonomous merge |
| Acceptance and risk review | Independent AI review with evidence | Required by delivery policy; not equivalent to a GitHub human approval |
| Human/code-owner approval | Effective remote rules | Respect if required; never bypass or impersonate |
| Master-only production | Release procedure and future enforcement | Current scripts alone do not enforce origin ancestry |

CI/protection configuration is a separate follow-up, not implemented by spec 016. Retained CODEOWNERS entries identify maintainers and may trigger actual approvals; they do not prove protections are enabled. The new policy supersedes a universally required Hugo review in prose, but does not change remote rules.

Use [code delivery](../.agents/skills/humami-code-delivery/SKILL.md) for integration decisions and [follow-ups](../ops/workflow-followups.md) for prerequisites. Zero checks is not a successful gate. Manual local tests do not substitute for configured required checks.
