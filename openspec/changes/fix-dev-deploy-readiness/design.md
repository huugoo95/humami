# Design — Spec 035

## Findings

The evidence comes from the repository and GitHub Actions logs only. No SSH access was used.

### Why DEV deploys fail

| Run | Probes | Curl result | Backend started |
|---|---|---|---|
| 39167692027 (2026-10-18) | 216 | all `curl: (60)` certificate name mismatch | — |
| 39167899404 (2026-10-18) | 216 | all `curl: (60)` | first log line 5 min 45 s after container start (overlapped prod deploy 0.5.2) |
| 39238051639 (2026-10-19) | 216 | all `curl: (60)` | `Started … in 172.8 seconds`, about 4 min before the window closed |
| 36258907466 (2026-09-26, last success) | — | `curl: (22) 502` while starting, then verified | — |

The 2026-09-26 run proves the vhost and network existed then, because it got HTTP 502 and not a TLS error. Production run 36266408122 then copied the repository `nginx.conf` and recreated `humami-nginx` at 20:04Z. Every later DEV run hits the `humami.es` certificate. The readiness window was long enough in the latest run, so lengthening it alone would not have fixed anything.

### Memory and CPU limits

| Service | Limits | Runtime flags |
|---|---|---|
| `dev-backend` | `mem_limit 384m`, `cpus 0.50` | `-Xms64m -Xmx192m -XX:MaxMetaspaceSize=128m -XX:ActiveProcessorCount=1` |
| `dev-frontend` | `mem_limit 192m`, `cpus 0.35` | `NODE_OPTIONS=--max-old-space-size=128`. `npm run start` keeps an extra npm process (about 40 MB) in the same limit. |
| `backend` (prod) | none | none: JVM default heap = 25% of host RAM, all CPUs |
| `frontend` (prod) | none | none |
| `nginx`, `certbot` (prod) | none | — |

DEV is capped, so it cannot starve production of memory. Production is uncapped, so a production spike can starve DEV, and DEV's 0.5 CPU makes its JVM start slowly. JIT compilation dominates Spring start under a CPU quota. C1-only compilation (`TieredStopAtLevel=1`) typically cuts that substantially, at a small cost in peak throughput, which DEV does not need. The Atlas round-trip of about 12 s mentioned during the 033 incident was not visible in the logs analyzed here. The Mongo monitors connected in about 24 s, which fits a throttled CPU and does not point to a network problem.

### Readiness window

`deploy-dev.sh` tries 72 times with `sleep 5` and no curl timeout. The real window was 7 min 12 s in run 39238051639. The documentation says 6 minutes. The window depends on curl latency, and a stalled request could hang it. The observed startup was 3 min 14 s from container start, or about 6 min under contention. A deadline of 480 s with 10 s per request gives margin and stays inside a 20-minute job, which already includes about 1.5 min of image pulls.

### Image size

- **Frontend**: the runtime stage runs `npm ci --omit=dev`. `playwright` and `playwright-core` are `devOptional` in the lockfile, so they are **not** installed in the runtime image. The weight is `next` itself, the `@next/swc-linux-arm64-musl` compiler binary (a non-dev optional dependency of `next`, unused at runtime), `sharp`/`@img` and the npm cache layer. With `output: 'standalone'`, the runtime keeps only traced files plus `node server.js`. On comparable apps this usually shrinks the image several-fold and also removes the npm wrapper process. It changes the production start command, `public/` and `.next/static` copying, and the optimizer cache path checked by `imageDelivery.test.ts`. It needs its own spec and a production smoke test.
- **Backend**: the runtime stage uses `eclipse-temurin:21-jdk-alpine`. A `21-jre-alpine` runtime avoids shipping the compiler toolchain (a JRE is typically a fraction of a JDK). The risk is low, but it is still a production image change and goes into the same follow-up spec.

### Disk

Production run 39240948149: 28 images (11.9 GB) before pruning, 7 in use (4.1 GB) afterwards, disk 97% → 57%. The 7 images in use include the stopped orphan `humami-dev-dev-mongo-1` (MongoDB image) left over from spec 021. Every `develop` push adds two images, and nothing removes them until the next production release. DEV should therefore prune, scoped to its own `dev-*` tags. A global `docker image prune -af` from the DEV script would also delete the previous production images, which the production prune already handles at release time.

### Sizing recommendation

- **Disk**: grow the root EBS volume from 19 GB to 30 GB gp3. This is an online resize plus `growpart`/`resize2fs`. It gives room for two releases, DEV and the build-cache churn, at a small monthly cost. Pruning alone leaves about 8 GB free, which is one bad week from full.
- **Instance**: two JVMs, two Next servers and nginx on one burstable ARM host. The instance type and RAM are not recorded in the repository and cannot be read without SSH. Before resizing, check CloudWatch `CPUCreditBalance` and memory over the DEV deploy windows (2026-10-18 21:30Z and 2026-10-19 10:34Z). If the host is a t4g.small (2 GiB), or credits reach zero during deploys, move to t4g.medium (4 GiB). That leaves room for an explicit production heap cap and for DEV `cpus: 1.0`. Record the instance type in `docs/` either way.

## Decisions

1. **Repository `nginx.conf` is canonical for both vhosts.** Production deploys already overwrite the host file, so a host-only fragment cannot survive. A host-local mounted `dev.conf` was rejected: if it were missing, Docker would create a directory at the bind path and nginx would fail to load `conf.d/*.conf`, which is a worse failure mode for production. The dev block keeps the existing `resolver 127.0.0.11` plus variable `proxy_pass`, so nginx starts even when DEV containers are down.
2. **Certificate preflight before pulling.** Missing dev certificate files abort the production deploy before any container changes. This mirrors the free-space preflight of spec 033.
3. **Imperative `docker network connect` after `up`** (guarded by `docker network inspect`), instead of declaring the external network in `docker-compose.images.yml`. An external network that is missing would make `compose up` fail for all of production. Creating it from the production project would give it labels that conflict with the DEV project, which owns it.
4. **DEV prune is tag-scoped.** The DEV script removes `humami-{backend,frontend}:dev-*` tags other than the target. `docker image rm` without `-f` refuses images that containers use, so the running DEV pins and all production images are safe by construction.
5. **Wall-clock deadline** using bash `SECONDS`, and the last curl exit code per URL in the timeout message.

## Rollback

Revert the PR. For production, redeploy the previous release, whose `nginx.conf` has no dev vhost. dev.humami.es then breaks again, and production is unaffected.
