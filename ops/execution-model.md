# Execution Model (Daily)

## Purpose
Keep work fast and consistent without bloating chat context.

## 1) Intake rule
Always start with:
`[TRACK] objective | done-criteria | deadline`

## 2) When to use direct execution vs sub-agents

### Use direct execution when
- task is small (single file/single command)
- low ambiguity
- can be finished in one focused pass

### Use sub-agents when
- task spans multiple domains (e.g., FE + BE + QA)
- likely >15-20 minutes
- requires parallelism (implement + review)

Suggested split:
- Agent A (executor): implementation
- Agent B (reviewer): checklist + risks + acceptance criteria

## 3) Skills policy

### Use existing skills for
- GitHub operations (`github` skill)
- Standard external workflows already codified

### Create Humami-specific skills only for repetitive flows
- Deploy
- Recipe publishing
- SEO cycle

If a flow repeats >=3 times with same pattern, promote to skill.

## 4) Memory policy

### Long-term memory
- Keep durable decisions in `ops/decisions.md` and root memory docs.

### Day-to-day logs
- Keep incidents and operational anomalies in `ops/incidents.md`.

### Retrieval in chat
- Use semantic memory retrieval (`memory_search` + `memory_get`) before relying on recollection.

## 5) Output discipline
For every completed task, report in this format:
- Context (1 line)
- Action (what changed)
- Result (evidence)
- Next (single next step)
