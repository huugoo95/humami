## 1. Definition and workflow

- [x] 1.1 Add the canonical production deployment OpenSpec and numeric index.
- [x] 1.2 Add ARM64 master build/publish and exact-tag deployment workflow.
- [x] 1.3 Document required secrets, serialization, verification and rollback.

## 2. Verification and delivery

- [x] 2.1 Validate workflow syntax and OpenSpec strict structure.
- [x] 2.2 Obtain independent review and resolve blocking findings.
- [ ] 2.3 Deliver through a PR to `develop`; deployment is activated only by a
  later merge/release to `master`.
- [ ] 2.4 Archive only after acceptance and a verified automated master run.
