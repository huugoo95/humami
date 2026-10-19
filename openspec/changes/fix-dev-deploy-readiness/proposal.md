# Spec 035 — Restore dev.humami.es routing and make DEV deploys reliable

## Problem and user impact

Every `Development images` run on `develop` has failed since 2026-10-18 (runs 39167692027, 39167899404, 39238051639) with `DEV health check timed out: home=000 meals=000 guides=000`. The dev environment no longer validates any merge, so regressions reach release branches unchecked.

The logs show that the timeout is not the cause. All 216 probes in each of the three runs failed with `curl: (60) SSL: no alternative certificate subject name matches target host name 'dev.humami.es'`. In run 39238051639 the backend reported `Started HumamiApplication in 172.8 seconds` at 10:37:48, and the probes went on failing until 10:41:46. The proxy serves the `humami.es` certificate for `dev.humami.es` because there is no longer a dev virtual host.

Root cause: spec 019 added the dev vhost (`deploy/dev/nginx.conf`) to the host's `/home/ubuntu/apps/humami/nginx.conf` by hand and attached `humami-nginx` to the `humami-dev-ingress` network by hand. Spec 026 then made every production deployment copy the repository `nginx.conf`, which has no dev vhost, over the host file and run `up --force-recreate`. The recreated container is on the production network only. The first automated production deployment (run 36266408122, 2026-09-26 20:04Z, release 0.5.1) recreated `humami-nginx`. The last successful DEV deploy (run 36258907466) ran before it, at 17:24Z.

Secondary weaknesses, which are not the cause of these failures:
- The DEV backend is CPU-starved: `cpus: 0.50`, 38 s before the first JVM log line and 173 s to start. In run 39167899404, which overlapped a production deploy, the first log line came 5 min 45 s after the container started. The 72 × 5 s readiness window (about 6–7 minutes, with no per-request timeout) has little margin.
- DEV pulls two new images on every `develop` push and never removes old ones. Only production deploys prune, so DEV images fill the shared 19 GB disk between releases. On 2026-10-19 the disk reached 97%.

Priority: high (the dev quality gate has been broken for three weeks).
Success: the next `develop` push produces a verified DEV deployment, and later production deploys keep dev.humami.es routable.

## User story

As the owner, when I merge to `develop`, the DEV deployment either verifies dev.humami.es or fails with a message that names the real cause. A production release does not silently break dev.

## Scope

1. **Routing survives production deploys** (touches production configuration, see Risks):
   - Move the dev server blocks from `deploy/dev/nginx.conf` into the repository `nginx.conf`, which becomes the single source of truth, and delete the fragment.
   - In `scripts/deploy-images.sh`, before pulling, abort if `certbot/conf/live/dev.humami.es/fullchain.pem` or `privkey.pem` is missing, so nginx is never recreated with a config it cannot load.
   - After `up`, connect `humami-nginx` to `humami-dev-ingress` when that network exists. This step is idempotent. If the network is missing, log a warning and continue: production is unaffected and dev returns 502.
2. **Explicit DEV readiness window** in `scripts/deploy-dev.sh`:
   - Replace the attempt count with a wall-clock deadline: `HUMAMI_DEV_READY_SECONDS`, default 480.
   - Give each probe `--max-time 10`.
   - On timeout, report the last curl exit code for each URL, so that a TLS error (60) is distinguishable from an unstarted app (22/7).
   - Raise the `deploy-dev` job limit from 15 to 20 minutes.
3. **DEV image hygiene** in `scripts/deploy-dev.sh`:
   - Before pulling and after verification, remove `ghcr.io/huugoo95/humami-{backend,frontend}:dev-*` tags that no container uses, except the tag being deployed.
   - Before pulling, require at least `MIN_FREE_MB` (default 2048) free.
   - Never prune production tags, other images or volumes.
4. **Faster DEV JVM start**: add `-XX:TieredStopAtLevel=1` (C1 only) to the DEV `JAVA_TOOL_OPTIONS`. The memory caps (`-Xmx192m`, `mem_limit 384m`) stay as they are.

## Non-goals

- Production JVM/Node memory caps or container limits. These need the host's RAM figure, which is not available from the repository or logs (see design).
- Next.js `output: 'standalone'` and a JRE runtime image. Both affect production images and belong in a separate change (see design).
- Resizing the instance or disk (see the recommendation in the design).
- Removing the orphan `humami-dev-dev-mongo-1` container and its volume. That is a data decision for the owner.
- Any deployment as part of this change.

## Acceptance criteria

- Given a production deployment with the dev certificate present, when it completes, then `humami-nginx` serves the dev vhost and is on `humami-dev-ingress`, and production smoke checks pass.
- Given the dev certificate is missing, when a production deployment starts, then it aborts before pulling and leaves the running containers untouched.
- Given a `develop` push, when DEV starts within 480 s, then the job reports `DEV deployment verified`.
- Given dev.humami.es keeps failing, when the deadline passes, then the job prints the last HTTP and curl exit codes per URL and the backend logs, and restores the previous pins.
- Given old `dev-*` images that no container uses, when a DEV deploy runs, then they are removed and the running DEV and production images remain.

## Risks and dependencies

- **Production nginx.** The production proxy will load a second vhost and certificate. If the dev certificate files disappear (for example after failed renewals followed by cleanup), nginx would fail to start and take humami.es down. The preflight check prevents a deploy from recreating nginx in that state. Renewal is unaffected: the default port-80 `humami.es` block already answers ACME challenges for `dev.humami.es`. The dev certificate expires on 2026-12-23, and the existing renewal cron covers it. Rollback: revert `nginx.conf` and `deploy-images.sh` and redeploy.
- **Network attachment.** Network attachment adds `humami-nginx` to a second bridge network. Dev containers can then reach nginx, as they already could before 2026-09-26. Production upstreams keep resolving on the default network.
- **Activation order.** Dev routing is restored only by the next production deployment that includes this change, or by a manual workflow dispatch of the production deploy. Until then, DEV deploys keep failing with curl 60, but the new timeout output names the TLS error.
