# dev-deployment

## ADDED Requirements

### Requirement: Production deployments preserve dev routing

A production deployment SHALL ship the dev.humami.es virtual host in the canonical proxy configuration. Before pulling, it SHALL verify that the dev certificate files exist and SHALL attach the proxy to the dev ingress network when that network exists. It MUST NOT recreate any container when the dev certificate files are missing.

#### Scenario: Release deployed with dev present

- **WHEN** a production deployment completes and `humami-dev-ingress` exists
- **THEN** `humami-nginx` serves dev.humami.es with its own certificate and is attached to `humami-dev-ingress`

#### Scenario: Dev certificate missing

- **WHEN** `certbot/conf/live/dev.humami.es/fullchain.pem` or `privkey.pem` is absent
- **THEN** the production deployment fails before pulling images and leaves running containers unchanged

#### Scenario: Dev network absent

- **WHEN** `humami-dev-ingress` does not exist
- **THEN** the production deployment logs a warning and completes its smoke checks

### Requirement: DEV readiness uses a bounded wall-clock window

The DEV deployment SHALL wait up to `HUMAMI_DEV_READY_SECONDS` (default 480) for `/`, `/meals` and `/api/guides`. It SHALL bound each probe to 10 seconds, and on timeout it SHALL report the last HTTP status and curl exit code for each URL.

#### Scenario: Slow but healthy start

- **WHEN** DEV becomes healthy within 480 seconds
- **THEN** the deployment records `deployment.json` and reports success

#### Scenario: TLS misrouting

- **WHEN** every probe fails with curl exit code 60 until the deadline
- **THEN** the failure output names exit code 60 for each URL, and the previous pins are restored

### Requirement: DEV deployments reclaim their own images

The DEV deployment SHALL remove `humami-backend` and `humami-frontend` images tagged `dev-*` that no container uses, except the target tag, both before pulling and after verification. Before pulling, it SHALL require `MIN_FREE_MB` (default 2048). It MUST NOT remove production tags, other images or volumes.

#### Scenario: Stale DEV images accumulated

- **WHEN** previous `dev-*` images exist that no container uses
- **THEN** they are removed, and the running DEV and production images remain

#### Scenario: Insufficient space

- **WHEN** free space after the DEV prune is below `MIN_FREE_MB`
- **THEN** the DEV deployment fails before pulling and prints disk usage

### Requirement: DEV JVM favours startup time

The DEV backend SHALL run with C1-only JIT compilation (`-XX:TieredStopAtLevel=1`) in addition to its existing memory caps.

#### Scenario: Compose definition

- **WHEN** `docker-compose.dev.yml` is rendered
- **THEN** `dev-backend` `JAVA_TOOL_OPTIONS` contains `-Xmx192m` and `-XX:TieredStopAtLevel=1`, and `mem_limit` remains `384m`
