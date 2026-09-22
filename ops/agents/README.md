# Temporary specialist delegation

The current task's primary agent coordinates; these profiles do not create persistent agents, user-owned tasks or worktrees. Read this index only when considering delegation, then only the selected profile.

| Work discovered | Profile |
| --- | --- |
| Unclear product scope or acceptance | [Product](product.md) |
| UI, navigation, accessibility, client integration | [Frontend](frontend.md) |
| Contracts, application logic, MongoDB, S3 | [Backend](backend.md) |
| Recipes or blog drafts | [Editorial](editorial.md) |
| Git delivery, publication or release execution | [Operations](operations.md) |
| Independent candidate assessment | [Reviewer](reviewer.md) |

Small tasks stay with the primary agent. Delegate only bounded work that can proceed independently; agree the API contract before parallel frontend/backend edits. Code integration requires an independent reviewer even when implementation is small.

Every delegated assignment states: objective; owned files/worktree and branch if editing; dependencies and relevant spec/skill/profile paths; acceptance evidence; allowed external actions; return format (result, findings, tests, files, blockers). Pass only relevant context. Discovery/reading is not publication authority.

Create temporary subagents with available delegation tools. If those tools are unavailable, do not claim independent review; report that gate as pending. A profile file alone does not instantiate an agent. Do not create another sidebar task unless the user explicitly asks.

Independent concurrent implementations require explicitly isolated worktrees/branches. Read-only reviewers may inspect the shared checkout. Avoid concurrent changes to shared instructions, dependency files, ports or databases; a worktree isolates files, not external services. Assign overlapping integration to the coordinator.

The coordinator inspects results, resolves contract conflicts, validates the combined candidate and reports the state of all active worktrees it created. Do not report the project clean by checking only the main checkout. On cancellation, stop workers before removing their worktrees; do not claim cancellation if it could not be verified.
