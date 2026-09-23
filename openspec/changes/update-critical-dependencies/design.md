# Design

## Inventory and decisions
Audit actual npm lockfile and Maven resolved runtime artifacts, not just manifest ranges. Consult vendor advisories and registries on2026-09-23; record applicability and fixed versions in validation.md. Keep React/Next and Spring-managed components aligned. Do not use force audit fixes or blanket dependency major upgrades.

Frontend target: Next15.5.26 (available registry patch), matching eslint-config-next; Node24 LTS across Docker/CI/local version declaration. The September22 Next RCE affects16.x, not15.x;15.5.26 includes hardening, while earlier July/August fixes do apply to the15.5.20 dependency range. Tooling and runtime findings are reported separately.

Backend: prefer compatible maintained patches/BOM upgrades, remove unused vulnerable DJL dependencies if source inspection confirms no use, and check resolved transitive versions. Document framework support gaps honestly; a patch update is not proof of commercial/OSS support. Do not change authentication or serialization contracts solely for dependency cleanup.

## Responsibilities and contracts
No API/DB contract changes intended. Backend full Maven verify includes real MongoDB pagination integration tests. Frontend npm ci, audit, unit tests, lint and production build validate the dependency update. Existing browser tests run where their fixtures/server configuration permits; unavailable checks are reported. CI uses isolated MongoDB and no production secrets. Fresh images are built/tested where Docker is available; source updates do not establish the currently deployed versions.

## Risks and rollback
Dependency changes can alter compilation, rendering or transitive resolution. Validate before integration, keep definition and implementation commits separate, and require independent review. Revert the upgrade commit if necessary; reverting reintroduces known risks and requires a replacement mitigation. Container OS/image and deployed-server vulnerability inventories are outside what a source-only audit can attest. Moving from Node18 is a deliberate runtime-major change to leave EOL support, not a UI feature migration.
