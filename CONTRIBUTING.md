# Contributing to Humami

Start with [AGENTS.md](AGENTS.md): it states how we work and what needs the owner's approval. Shared engineering constraints live in [ENGINEERING_RULES.md](ENGINEERING_RULES.md).

- Product features: [feature spec](.agents/skills/humami-feature-spec/SKILL.md), then [development](.agents/skills/humami-development/SKILL.md).
- Fixes and technical changes: [development](.agents/skills/humami-development/SKILL.md) directly.
- Branch, PR and merge: [code delivery](.agents/skills/humami-code-delivery/SKILL.md). Merging to `master` deploys.
- Production verification and recovery: [release](.agents/skills/humami-release/SKILL.md).

There is no dev environment. Run the frontend locally with `npm run dev` (it can use the production API, which allows CORS from `localhost:3000`) or the full stack with `docker-compose.yml`.
