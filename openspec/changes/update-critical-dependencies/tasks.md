## 1. Audit and definition
- [x] 1.1 Record baseline versions, advisory sources, applicability and fixed-version targets.
- [x] 1.2 Validate the OpenSpec definition and commit separately.

## 2. Remediation
- [x] 2.1 Apply compatible frontend fixes, align lint package and update Node runtime.
- [x] 2.2 Remediate backend critical/high findings, preserving contracts and recording support limitations.
- [x] 2.3 Update CI to validate both modules and current action runtimes.

## 3. Validation and delivery
- [x] 3.1 Verify post-change dependency graph/audits, tests, lint and builds; record residual risks.
- [x] 3.2 Obtain independent candidate review and resolve findings.
- [ ] 3.3 Push a PR to develop and integrate only after remote checks pass.
- [ ] 3.4 Archive only after acceptance.
