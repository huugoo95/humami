# Design

`smoke-prod.sh` owns production readiness. Its default timeout is raised from
90 seconds to 480 seconds and callers may still override
`WAIT_TIMEOUT_SECONDS` or `WAIT_INTERVAL_SECONDS` for a diagnostic run.

The script bounds each `curl` connection attempt to ten seconds and the entire
request to fifteen seconds. It preserves the same readiness endpoint and full
smoke endpoints, keeping the output useful for investigation.

`deploy-images.sh` invokes the smoke script once. This replaces the outer
five-attempt loop, whose repeated timers obscured the total wait and could
interact poorly with the GitHub Actions job timeout. A single eight-minute
window fits comfortably inside the existing 20-minute deployment job while
leaving the job to fail deterministically for a genuine outage.

No API, database, schema, image or secret contract changes are required.
