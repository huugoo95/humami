# Spec 030 — Restore the frontend dependency audit gate

## Why

Frontend CI stopped at `npm audit --audit-level=high` on an unchanged lockfile after new upstream advisories (51 findings: 1 critical, 42 high), blocking every frontend PR (first seen on PR #80). Most findings have patched releases. One, braces GHSA-vfj7-8cjw-p6xm, affects every published braces version (<=3.0.3) and reaches us through micromatch in tailwindcss 3 and eslint-config-next. It is still present in eslint-config-next 16.4.2, the latest version, so no upgrade removes it.

## Scope

- Apply non-breaking lockfile fixes (`npm audit fix`), clearing the production advisories (axios, sharp, source-map-js, handlebars and others).
- Upgrade the test toolchain to Jest 30 (`jest`, `@types/jest`, `ts-jest` 29.4.15). Jest 30 uses picomatch instead of micromatch, removing its chain of high advisories.
- Replace the CI audit step with two gates:
  - `scripts/npm-audit-gate.mjs`: fails on any high/critical advisory unless `humami-web/audit-allowlist.json` has an unexpired, justified entry for it.
  - `npm audit --omit=dev --audit-level=high`: production dependencies must be clean, with no exceptions.
- Allowlist only GHSA-vfj7-8cjw-p6xm until 2027-01-18. It is build-time tooling with repository-controlled glob patterns and is absent from production dependencies.

Out of scope: the Tailwind 4 migration. It would not remove the advisory, because eslint-config-next still carries it, and it changes styling across the site. Moderate advisories are not gated, as before.

## Acceptance criteria

- Frontend CI passes both audit gates, then tests, lint, build and the image smoke test.
- An unlisted or expired high/critical advisory fails the gate.
- `npm audit --omit=dev` reports no high/critical advisories.
- Existing frontend tests pass on Jest 30.
