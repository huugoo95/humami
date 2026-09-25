# Operating knowledge

- docs/ retains shared product, brand, domain and architectural knowledge.
- .agents/skills/ owns repeatable procedures, with selective reference loading.
- ops/ retains delegation profiles, decisions, incidents, publication records and follow-up dependencies.
- scripts/ owns executable mechanics. Never store credentials here.

Start from [task routing](../AGENTS.md). Consult [delegation](agents/README.md) only when splitting work, [decisions](decisions.md) for durable choices and [incidents](incidents.md) for actual incidents. Procedural runbooks were removed after migration into their owning skills; shared deployment topology remains in docs/ghcr-deploy.md.

[Workflow follow-ups](workflow-followups.md) track prerequisites that this instruction migration does not implement. Content records are created only for actual operations, with source, target and result but without secrets.
