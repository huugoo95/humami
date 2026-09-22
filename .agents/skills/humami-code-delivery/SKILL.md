---
name: humami-code-delivery
description: Prepare branches and deliver Humami changes through commits, push, PR and gated integration. Use for code delivery or the delivery phase of implementation; not production deployment.
---

# Deliver a change

## Working branch
1. Inspect `git status`, current branch and `git worktree list`. Preserve unrelated work. Fetch the intended remote when possible and report stale references if it fails. Read [branch and commit conventions](references/git-conventions.md).
2. Retain the valid branch for this task. Otherwise create the appropriate branch before editing: feature/fix/chore and ordinary docs from updated develop; hotfix and its production-linked documentation from master; release preparation from develop. Features use `feat/<spec-id>-<short-name>`, never an automatic replacement prefix. Do not silently switch a directory used by another active worker; isolate concurrent implementations explicitly.
3. A definition/local-only request stops after its requested local artifacts. An implementation request normally includes delivery through develop, subject to the gates below.

## Commit and PR
1. Inspect the actual diff and untracked files; stage only this task's files, not unrelated work. Use conventional commit prefixes and separate definition from implementation when already planned that way.
2. Verify remote identity and explicit destination before push. Use `git push -u origin HEAD:<working-branch>` with the verified branch substituted; never bare push when upstream might still point to develop. No force push or direct commits to long-lived branches.
3. Use an available authenticated GitHub connector or CLI to create/update the PR. Feature/fix/chore and ordinary docs target develop. Release targets master then back-merges to develop; hotfix targets master then develop. Documentation explicitly tied to a production hotfix follows that hotfix scope: PR to master, then back-merge to develop. Include numeric ID/OpenSpec link, scope, evidence, applicable TDD note, risks and rollback using the [PR template](../../../.github/pull_request_template.md). Do not claim a PR exists if tool/auth access is absent.

## Integration gate
- Obtain [independent review](../humami-code-review/SKILL.md) and record the reviewed candidate. Resolve blocking findings; identify any residual risks explicitly.
- Verify required checks are configured and successful for the candidate, actual protections/approvals allow merging, and the PR is mergeable. Zero checks or unknown protection state is not success. Refresh affected review/checks after edits or base updates.
- Prefer squash for feature/fix integration; preserve Git Flow ancestry for release/hotfix and back-merges. Merge only within the requested scope, using the verified candidate SHA when the tool supports it. Verify remote merge state afterward.
- If checks, protections, auth or independent review are missing, leave the PR pending (or local commits if PR creation is unavailable), identify the blocker and reference [enforcement prerequisites](../../../docs/enforcement-matrix.md). Do not weaken protections or impersonate human approval.

Report separately: local branch/commit, push, PR URL, merge destination and checks. No deployment is included. Update the active change's tasks with evidence; leave archive pending until implementation is accepted.
