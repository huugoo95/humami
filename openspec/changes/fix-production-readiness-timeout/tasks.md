## 1. Definition

- [x] 1.1 Define bounded production readiness behavior and its operational
  limits.

## 2. Implementation

- [x] 2.1 Make the smoke script own one eight-minute readiness window.
- [x] 2.2 Bound HTTP requests and remove the repeated outer smoke loop.

## 3. Verification and delivery

- [x] 3.1 Validate shell syntax and exercise zero- and one-second timeouts
  against an unavailable local endpoint. `openspec` CLI is unavailable.
- [x] 3.2 Complete independent review with no blocking findings.
- [ ] 3.3 Integrate locally into `develop` without pushing or deploying.
