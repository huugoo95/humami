## 1. Define and prepare

- [x] 1.1 Validate the OpenSpec definition and review the current local dev/Atlas topology.
- [x] 1.2 Create a dedicated Atlas database user and safely store its dev-only URI on the server.

## 2. Migrate and configure

- [x] 2.1 Back up and inventory the local `humami_dev` database; restore it to Atlas and compare inventory.
- [x] 2.2 Update dev Compose configuration to require the dedicated Atlas URI and remove local MongoDB runtime dependency.
- [x] 2.3 Review, deliver and integrate the configuration change through `develop`.

## 3. Activate and verify

- [x] 3.1 Cut dev backend over to Atlas; retain but stop the local MongoDB fallback.
- [x] 3.2 Verify dev data/endpoints/security and unchanged production state; record redacted evidence.
- [ ] 3.3 Archive only after acceptance.
