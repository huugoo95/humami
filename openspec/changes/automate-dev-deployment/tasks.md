## 1. Definition

- [x] 1.1 Validate OpenSpec artifacts and record the deployment prerequisites.

## 2. Automation

- [x] 2.1 Add a post-image GitHub Actions DEV deployment job with explicit secret contracts and develop-only scope.
- [x] 2.2 Add a remote deployment script that performs registry login/pull/logout, digest pinning, isolated activation and bounded health checks.
- [x] 2.3 Add secret-safe deployment evidence and failure diagnostics.

## 3. Validation and delivery

- [x] 3.1 Validate workflow YAML and remote script syntax; exercise dry-runable input validation.
- [ ] 3.2 Obtain independent review, deliver a PR to `develop`, configure required secrets and verify an end-to-end DEV deployment.
