# Contributing to Humami

Start with [AGENTS.md](AGENTS.md) for task routing. Procedures are maintained in project skills; shared engineering constraints remain in [ENGINEERING_RULES.md](ENGINEERING_RULES.md).

- New product/code definitions use [OpenSpec feature definition](.agents/skills/humami-feature-spec/SKILL.md). Historical specs remain available; numeric index files link to canonical OpenSpec changes.
- Implement with [development](.agents/skills/humami-development/SKILL.md).
- Review with [independent review](.agents/skills/humami-code-review/SKILL.md).
- Branch, commit, PR and integrate with [code delivery](.agents/skills/humami-code-delivery/SKILL.md).
- Prepare a production release with [release](.agents/skills/humami-release/SKILL.md).

Definition-only requests do not start implementation. Pure docs maintenance is exempt from a spec unless it changes product behavior, contracts or release policy. All repository changes land through PRs, not direct commits to develop/master.

Required-check enforcement is a separate prerequisite; see [enforcement status](docs/enforcement-matrix.md). Missing gates must be reported, not silently waived. Track actual progress in the active OpenSpec tasks and keep PROGRESS.md as a concise project summary.
