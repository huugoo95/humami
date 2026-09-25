# Significant-task intake and closure

Migrated from ops/work-mode.md; applies across domains, not just programming.

A significant task changes repository files, affects staging/production, needs a PR, takes more than approximately 15 minutes, or requires explicit validation evidence.

Before execution state `[TRACK] objective | done-criteria | deadline`. Choose one primary track: BE/API, FE/UX, Infra/Deploy, Data/Recipes or SEO/Growth, and identify dependent areas. Use the [brief template](../../../../docs/brief-template.md) required by the project; extract supplied fields from the request instead of asking for them again. Mark an unspecified deadline as not provided, never invent it. Clarify missing information only if it blocks the work.

Handle small/simple work directly; use implementer and reviewer for complex or multi-domain work, with delegation rules in the [specialist index](../../../../ops/agents/README.md). Code integration always requires independent review.

Before closing:
- Check acceptance criteria and capture applicable tests, logs, screenshots or URLs.
- Verify the requested branch/PR/spec workflow and report actual delivery state.
- Record durable decisions in [decisions](../../../../ops/decisions.md) and actual incidents in [incidents](../../../../ops/incidents.md).
- Update the active OpenSpec task evidence and the project progress summary after relevant blocks.

Report Context, Action, Result and Next briefly, with blockers. Distinguish edits, commits, push, PR, merge, publication and deploy. Account for every worktree created for the task; one clean checkout does not prove there is no outstanding work elsewhere.
