# Process versus enforcement

| Requirement | Enforced by |
| --- | --- |
| Tests, lint, build, dependency audit | Backend CI and Frontend CI on every PR to `master` and every push to `master` |
| Production images build | Production deployment workflow on PRs to `master` (build only) |
| Only `master` reaches production | The production workflow deploys pushes to `master` with exact-SHA images |
| Production health | `scripts/smoke-prod.sh` after every deploy, with automatic rollback to the previous release on failure |
| Review of high-risk changes | Independent AI review per [code review](../.agents/skills/humami-code-review/SKILL.md); not a GitHub approval |

GitHub branch protection is not configured. Merging with failing or pending checks is a process violation even though GitHub would allow it.
