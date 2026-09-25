# Spec 014: monorepo-deploy-packaging-and-server-bootstrap

- **Status:** Draft
- **Owner:** Hugo + tenacitas
- **Created:** 2026-03-28

## 1) Problem
Humami is currently a monorepo with separate backend and frontend runtimes, but the deploy/package model is not fully consistent with that reality.

Current pain points:
- GHCR publishing works conceptually, but backend packaging is misaligned with repo structure.
- Deployment knowledge is split between repo files, server state, and ad-hoc operational memory.
- There is uncertainty about what infra/runtime config should live in the repository versus only on the server.
- Server migration risk is higher than desired because reproducibility is not yet fully formalized.

## 2) Goal and metric
Define and implement a deploy model for Humami that is:
- reproducible,
- portable across servers,
- low-friction for deploys,
- aligned with the current monorepo shape,
- simplified for a production topology where MongoDB is external (Atlas).

Success metrics:
1. A fresh server can be bootstrapped using repository-tracked infra/runtime files plus external secrets.
2. Backend and frontend images can be built and pushed to GHCR from the monorepo without ad-hoc local workarounds.
3. Production deploy can run from prebuilt images with a documented and repeatable process.
4. Nginx and deploy config drift is minimized because reproducible config is versioned.
5. Production deploy no longer depends on a server-local MongoDB container when Atlas is the chosen production database.
6. TLS issuance and renewal are documented and automated enough to avoid server-specific manual memory.

## 3) Scope
### In scope
- Define the recommended packaging/deploy strategy for the current monorepo.
- Standardize Humami runtime artifacts as separate images:
  - backend image
  - frontend image
- Define what infra/runtime config belongs in repo versus outside repo.
- Make backend/frontend image build flow compatible with monorepo layout.
- Document server bootstrap/migration expectations.
- Document production assumptions for Atlas as the database backend.
- Clarify TLS/SSL ownership, issuance, renewal, and nginx reload behavior.
- Update deploy docs and runbooks accordingly.

### Out of scope
- Full migration to multi-repo.
- Full infrastructure-as-code (Terraform/Ansible/Kubernetes).
- Replacing nginx with another reverse proxy right now.
- Reworking application architecture beyond what is necessary for packaging/deploy clarity.

## 4) Recommended direction
Humami should remain a **monorepo** for now, while publishing **two deployable images**:
- `ghcr.io/<owner>/humami-backend:<tag>`
- `ghcr.io/<owner>/humami-frontend:<tag>`

This keeps product/engineering context centralized while preserving operational separation of runtime services.

## 5) Repo vs non-repo boundary
### Must live in repo
- nginx config/templates used for reproducible deploys
- compose files for source-build and image-based deploys
- deployment scripts
- bootstrap/check/predeploy scripts
- runbooks and migration docs
- non-sensitive defaults and documented directory conventions
- image build definitions (Dockerfiles or equivalent packaging files)
- TLS issuance/renewal/reload workflow documentation and scripts/templates (without secrets)
- clear distinction between production topology and local/dev topology

### Must stay outside repo
- real production `.env`
- TLS certificates and private keys
- registry credentials/tokens
- SSH private keys
- machine-specific secrets
- Atlas credentials / production DB secrets

## 6) Functional requirements
### FR1 — Monorepo-aligned image packaging
Given the Humami monorepo layout,
when backend and frontend images are built,
then the build definitions must work without temporary manual file-copy workarounds.

### FR2 — Separate runtime images
Given the production runtime,
when deploying Humami,
then backend and frontend must be deployable as separate images/tags.

### FR3 — Versioned reproducible infra config
Given a new or replacement server,
when setting up Humami,
then nginx/deploy/runtime config required for a standard deploy must be available from the repository.

### FR4 — Secret isolation
Given production deployment,
when reproducing the environment,
then secrets and real credentials must not be stored in the repository.

### FR5 — Portable server bootstrap
Given a fresh server,
when following the documented bootstrap process,
then an operator must be able to prepare the host, supply secrets, pull images, configure nginx/certs, and bring the stack online without relying on undocumented server-only state.

### FR6 — Deploy path from prebuilt images
Given published GHCR images,
when deploying to production,
then the server should use image pulls instead of full source rebuilds by default.

### FR7 — Production database externalization
Given production deployment,
when Humami is brought online,
then it must connect to Atlas (or another external managed MongoDB) rather than relying on a local MongoDB container.

### FR8 — Local database retention for development
Given local development,
when developers run Humami locally,
then a local MongoDB container may still be supported as a convenience path.

### FR9 — TLS automation clarity
Given HTTPS production hosting,
when certificates approach expiry,
then the issuance/renewal/reload process must be documented and automatable without relying on memory of one specific server.

## 7) Acceptance criteria
1. Given the current monorepo,
   when the release build/push flow is executed,
   then backend and frontend images can be built and pushed from the repo structure as-is.

2. Given a production server replacement,
   when an operator follows the bootstrap/deploy runbook,
   then the stack can be recreated using repo-tracked config plus external secrets.

3. Given nginx config changes,
   when they are needed for deploy/runtime behavior,
   then they are represented in version-controlled files rather than existing only as server-local edits.

4. Given sensitive values,
   when reviewing the repository,
   then production secrets/certs are not committed.

5. Given image-based deploy mode,
   when `docker-compose.images.yml` is used,
   then backend and frontend run from separate GHCR images under the same release tag strategy.

6. Given production deployment,
   when the stack definition is reviewed,
   then MongoDB is not required as a production-side container if Atlas is the chosen production data store.

7. Given HTTPS production hosting,
   when an operator reviews the deploy/bootstrap documentation,
   then certificate issuance and renewal steps are explicit enough to be automated and repeated on a new server.

## 8) Technical design
### 8.1 Packaging model
Preferred model:
- keep monorepo
- publish two images
- use one release tag per repo snapshot/commit

This means FE and BE share a release identifier while remaining operationally separable.

### 8.2 Build-definition strategy
The build layer must reflect monorepo reality.
Potential implementation shapes:
- Dockerfiles adjusted to root-context builds with explicit subpaths, or
- dedicated packaging Dockerfiles under a top-level `docker/` or similar folder.

The chosen approach must avoid context mismatch between root-level shared tooling and service-level code.

### 8.3 Infra config strategy
Version infra/runtime config that is required to reproduce a standard deploy:
- nginx config
- image-based compose file
- source-build compose file (if retained)
- deploy scripts
- bootstrap docs/checklists
- TLS issuance/renewal/reload workflow docs and helper scripts/templates

Production and local topology should be intentionally different where useful:
- production may use Atlas and therefore omit local MongoDB container requirements
- local/dev may still retain MongoDB container support for convenience

### 8.4 Secret model
Use external secret injection for:
- app env values
- registry auth
- certs/keys
- Atlas credentials

The repository may include examples/templates, but never real secret values.

## 9) Risks and dependencies
### Risks
- Drift between source-build deploy path and image-based deploy path.
- Overcomplicating packaging if repo structure is changed too aggressively.
- Hidden server assumptions (firewall, certbot paths, system packages, directory layout).
- TLS renewal becoming operationally fragile if partially automated and partially manual.

### Dependencies
- Valid GHCR credentials with package write permissions.
- Stable Dockerfiles/build definitions.
- Updated deploy documentation.
- Clear production decision on Atlas connection/env contract.

## 10) Proposed implementation tasks
### Track: Infra/Deploy
- Audit and fix backend image build definition for monorepo compatibility.
- Audit and fix frontend image build definition for consistency and cache hygiene.
- Update `scripts/release-build-push.sh` to use the chosen monorepo packaging strategy.
- Validate `docker-compose.images.yml` against published image naming/tag strategy.
- Split local-vs-production runtime assumptions clearly (Atlas in prod, optional Mongo container in local).
- Define and document server bootstrap checklist.
- Define and document TLS issuance/renewal/reload flow.
- Update deploy runbooks/docs to formalize repo-vs-secret boundary.

### Track: Cross-track docs
- Update `docs/ghcr-deploy.md`
- Update `ops/runbook-deploy.md`
- Add explicit “new server bootstrap / migration” documentation
- Add explicit HTTPS/TLS operations notes

## 11) Open decisions
1. Should packaging Dockerfiles remain inside `humami-backend/` and `humami-web/`, or move to a top-level deploy/docker folder to better represent monorepo builds?
2. Should `latest` continue to be published, or only immutable commit/release tags in production flows?
3. Should nginx remain host-mounted from repo files, or be packaged into a custom image later if infra maturity increases?
4. Should TLS automation remain nginx + certbot, or later move to a simpler TLS-terminating option if operational complexity becomes a recurring pain?

## 12) Recommendation
Immediate recommendation:
- keep monorepo,
- publish two images,
- treat Atlas as the production MongoDB backend,
- keep local Mongo container support only for local/dev convenience,
- version nginx/deploy/TLS workflow config in repo,
- keep secrets/certs out of repo,
- formalize a server bootstrap/migration path,
- fix packaging so GHCR flow works natively from repository structure.
