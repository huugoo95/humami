# Validation

Validated on 2026-09-26 against `origin/develop`:

- `bash -n scripts/smoke-prod.sh` and `bash -n scripts/deploy-images.sh`
  passed.
- `git diff --check` passed.
- `WAIT_TIMEOUT_SECONDS=0 ./scripts/smoke-prod.sh http://127.0.0.1:9`
  exited with status 1 and reported HTTP 000 without stale response content.
- The same unreachable endpoint with a one-second timeout exited in one second,
  confirming that the request and sleep do not exceed the configured window.
- Independent review found no blocking issues after the deadline and temporary
  file handling were corrected.

The OpenSpec CLI was not installed. A positive HTTP-server test and Compose or
SSH execution were not run because the sandbox does not permit binding a local
port; no production operation is part of this local-only delivery.
