---
name: humami-code-delivery
description: Branch, commit, open PRs and merge Humami changes to master (trunk-based). Merging deploys to production automatically.
---

# Deliver a change

## Branch
1. Inspect `git status`, the current branch and `git worktree list`; preserve unrelated work. Fetch `origin`.
2. Create a short branch from updated `origin/master` using the [conventions](references/git-conventions.md), or keep the task's existing branch.

## Commit and PR
1. Stage only this task's files. Use conventional commit prefixes.
2. Push with `git push -u origin HEAD:<branch>`. Never force-push `master` or commit to it directly.
3. Open the PR to `master` with the [template](../../../.github/pull_request_template.md): what and why, evidence, risk and rollback.

## Merge
- Wait for every check on the PR head to finish green. Do not poll in a tight loop; wait in the background.
- High-risk changes need an [independent review](../humami-code-review/SKILL.md) before merge.
- Squash merge with `--match-head-commit <sha>`; the owner's standing authorization covers this when CI is green (see [AGENTS.md](../../../AGENTS.md)).
- Merging starts the production deployment; continue with [release](../humami-release/SKILL.md) to follow and verify it.
- Delete the merged branch locally.
