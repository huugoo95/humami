# Spec 028 — Production deployment readiness timeout

## Problem and goal

The production deployment performs several short smoke-check windows. A slow
but healthy backend startup can therefore exhaust the workflow budget or leave
an ambiguous series of retries. As the owner, I want one explicit readiness
window so the workflow waits long enough for a normal startup and reports a
clear failure if it remains unavailable.

Priority: high operational reliability. Success: a production deployment gives
the API up to eight minutes to become ready, remains within the 20-minute
workflow limit, and performs the complete smoke suite exactly once after
readiness succeeds.

## User story

As the owner, when production starts more slowly than usual after a release,
I receive a successful deployment once it becomes healthy within the supported
readiness window instead of a false failed workflow.

## Scope

- Replace repeated short smoke runs with one configurable, bounded readiness
  wait.
- Set the production default readiness window to eight minutes.
- Bound each HTTP request so a stalled network call cannot wait indefinitely.
- Keep the existing public endpoints and failure output.

## Non-goals

- No change to application startup, Docker resources, registry access, image
  tags, production data, or automatic rollback.
- No production deployment in this change.

## Risks and dependencies

The deployment job retains a 20-minute limit, so the eight-minute readiness
window leaves time for image retrieval and Compose recreation. A real outage
will be reported after the bounded wait; it is not masked by unlimited retries.
The change depends on `curl`, already used by the smoke script.

## Acceptance criteria

- Given a normal but slow application startup, when the API returns HTTP 200
  within 480 seconds, then the deployment SHALL continue to the full smoke
  suite without restarting a separate readiness timer.
- Given the API remains unavailable for 480 seconds, when the readiness wait
  expires, then the deployment SHALL fail with the final HTTP status and the
  configured timeout.
- Given an individual HTTP request stalls, when it exceeds 15 seconds, then
  the smoke script SHALL continue its bounded readiness logic or fail the
  final check instead of hanging indefinitely.
