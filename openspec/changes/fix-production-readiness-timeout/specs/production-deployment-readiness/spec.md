# production-deployment-readiness

## ADDED Requirements

### Requirement: Bounded production readiness verification

The production deployment SHALL use one bounded readiness wait before the
complete public smoke suite. By default it MUST allow 480 seconds for the API
to become available and MUST bound individual HTTP requests. It MUST report a
failure with the final response status when readiness does not succeed.

#### Scenario: Slow healthy startup

- **WHEN** the production API returns HTTP 200 within 480 seconds after the
  Compose update
- **THEN** the deployment SHALL run the complete public smoke suite and report
  success without restarting the readiness timeout

#### Scenario: Unhealthy deployment

- **WHEN** the production API does not return HTTP 200 before the readiness
  timeout expires
- **THEN** the deployment SHALL fail with the configured timeout and last HTTP
  status
