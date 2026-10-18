# Validation

Validated on 2026-10-18 against candidate d2be5d6 (local only; no production contact).

- The workflow YAML parses (js-yaml, and Ruby YAML in the independent review).
- Deploy step simulation: the `run` block was extracted from the workflow and run with fake `ssh`/`scp` that map `/home/ubuntu/apps/humami` to a temporary directory, with the final `deploy-images.sh` call stubbed.
  - Correct copy: `docker-compose.images.yml` and `nginx.conf` land in the root, both scripts land in `scripts/`, `sha256sum --check --strict` prints OK for both, and the deploy stub runs.
  - Tampered remote `scripts/smoke-prod.sh`: the check prints FAILED and the step stops before `docker login` or the deploy.
- Independent review (d2be5d6) found no blocker or major issues. It confirmed:
  - Quoting: the two checksum lines reach the remote shell intact, with no injection risk.
  - The token on ssh stdin is still read by `docker login`.
  - `set -euo pipefail` aborts before any container change.
  - Worst-case smoke time is about 9 minutes, well within the job's 20-minute limit.

Not performed: a real GitHub Actions run, contact with the production host, or confirming that `sha256sum` exists on the host (if it is missing, the step fails before deploying).

Operational follow-up: the stale `deploy-images.sh` and `smoke-prod.sh` copies in the host app root are no longer used and can be removed by hand. The 480 s readiness window will be confirmed on the next production deployment.
