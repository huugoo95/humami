# Dependency review and validation — 2026-09-23

## Actual versions and decisions
| Component | Before | After | Reason |
| --- | --- | --- | --- |
| Next.js | 15.5.20 | 15.5.26 | Critical/high advisories in installed range; stay on15.x |
| eslint-config-next | 15.1.7 | 15.5.26 | Align framework tooling |
| Next nested PostCSS | 8.4.31 | 8.5.28 | Scoped override within8.x; eliminate remaining high finding without Next16 migration |
| Handlebars (tooling) | 4.7.8 | 4.7.9 | Critical tooling advisory; compatible lockfile refresh |
| sharp | 0.34.5 | 0.35.4 | Next-supported patched image dependency; verified baseline in lockfile |
| Frontend Node image | 18-alpine | 24-alpine | EOL runtime replaced with LTS; local .nvmrc/engines and CI aligned |
| Spring Boot | 3.4.0 | 3.5.16 | Compatible baseline removes vulnerable transitive versions |
| Tomcat | 10.1.33 | 10.1.60 | Newer security fixes than Boot BOM |
| Netty | 4.1.115.Final | 4.1.138.Final | Newer security fixes than Boot BOM |
| DJL api/tokenizers | 0.24.0 | removed | Critical advisories; no source usage (embeddings use HTTP WebClient) |
| CI actions | checkout4/setup-java4 | checkout6/setup-java5; setup-node6 | Supported Node24-based action runtime |

React19.1.0, AWS S3 SDK2.20.130, Java21 and application marketing/artifact versions remain unchanged: no applicable direct critical fix demonstrated. Java runtime patch inventory on deployed hosts and container OS scanning were not performed. Images of nginx/certbot are floating tags, so their deployed versions cannot be inferred from source. No deployment is included.

## Security evidence and limits
Baseline npm audit:20 dependency entries (2 critical,10 high,4 moderate,4 low); these are dependency-level findings, not counts of unique exploitable vulnerabilities. Final npm audit with committed lockfile:0 findings, including tooling. No --force upgrades used. PostCSS override only targets Next's nested dependency; removing its stale lock entry and resolving via npm produced8.5.28, verified by npm ls.

Baseline Maven tree:137 coordinates. OSV batch matches136 distinct advisory IDs:10 critical,50 high,60 moderate,16 low. Primary DJL September advisory was not yet indexed by OSV and was checked separately. Candidate full tree:131 coordinates,0 critical/high,4 moderate unique advisories. Package matching is not proof of exploitability; Netty server-specific issues and conditional Tomcat partial-PUT attacks are not claimed reachable. Multipart processing is relevant to image uploads.

Remaining moderate findings, deliberately outside critical/high remediation:
- log4j-api2.24.3: GHSA-qv9r-c865-cp47 (fixed2.25.5).
- jackson-databind2.21.4: GHSA-5gvw-p9qm-jgwh, GHSA-5jmj-h7xm-6q6v, GHSA-mhm7-754m-9p8w (fixed2.21.5).
No application references to the cited specialized Jackson annotations/options or Log4j MapMessage were found; dependency-internal applicability is not ruled out.

Spring Boot3.5.16 is the last OSS3.5 release, not a supported long-term baseline. Migration to supported Boot4 or commercial support remains a separate follow-up. Targeted Tomcat/Netty overrides mitigate today's identified critical/high findings, not future ones. This audit does not claim production is remediated until a separately authorized master release is deployed.

## Primary sources
- https://nextjs.org/blog/august-2026-security-release
- https://nextjs.org/blog/nextjs-security-update-september-22-2026 (September22 RCE affects16.x;15.5.26 hardening only)
- https://github.com/handlebars-lang/handlebars.js/security/advisories/GHSA-2w6w-674q-4c4q
- https://github.com/postcss/postcss/security/advisories/GHSA-r28c-9q8g-f849
- https://nodejs.org/en/about/previous-releases
- https://spring.io/blog/2026/06/25/spring-boot-3-5-16-available-now/
- https://tomcat.apache.org/security-10.html
- https://github.com/netty/netty/security/advisories/GHSA-pvjx-v7vp-62vq
- https://github.com/deepjavalibrary/djl/security/advisories/GHSA-cqqg-r2fh-7jjm
- https://github.com/deepjavalibrary/djl/security/advisories/GHSA-jcrp-x7w3-ffmg
- https://logging.apache.org/security.html
- https://github.com/FasterXML/jackson-databind/security/advisories/GHSA-5gvw-p9qm-jgwh

## Reproduction and checks
- `npm ci`, `npm audit --json`, `npm test`, `npm run lint`, `npm run build` in humami-web on Node24.21.0; runtime downloaded from nodejs.org and SHA256 checked.
- `JAVA_HOME`21, isolated MongoDB8.0.15 on127.0.0.1:27177: `mvn -f humami-backend/pom.xml -Dhumami.test.mongoUri=mongodb://127.0.0.1:27177 verify`, with SPRING_DATA_MONGODB_URI pointing to the test instance:42 tests passed,0 failures/errors/skips, package successful.
- Maven graph via `mvn dependency:tree`; unique groupId:artifactId/version pairs queried through https://api.osv.dev/v1/querybatch with ecosystem Maven, then advisory details fetched for severity. Baseline and candidate graphs are generated from their respective pom commits; OSV is point-in-time and can lag primary disclosures.
- No new runtime behavior added: security audit failures are the RED evidence, patched audits and existing regression tests are GREEN; artificial tests asserting version strings were not added.
- Frontend Docker context excludes host node_modules, build artifacts and .env files; image build is validated by CI because local Docker daemon is unavailable. Frontend CI also runs audit/tests/lint/build and starts the container to require HTTP200 on its homepage. Existing Playwright smoke defaults to production and was not used to claim candidate validation. Existing backend CI retains real MongoDB tests.
- OpenSpec strict validation passed. Final local frontend checks pass:2 unit tests, lint (one pre-existing img warning), production build. Independent review found no dependency/lockfile/workflow findings; final context/smoke additions and remote checks pending.

## Container regression found by CI
Initial frontend run35865829420 passed audit/tests/lint/build and image construction, but runtime smoke failed: next.config.ts required TypeScript (dev-only), triggering an attempted install as non-root. Configuration was converted to equivalent next.config.mjs with JSDoc typing and Docker COPY adjusted; image options and production dependency scope are unchanged. The smoke test remains the regression check and must pass before merge. This is a compatibility correction required by the container validation, not new product behavior.

Independent review confirmed the config conversion preserves options and non-root runtime, with no findings. Local lint/build pass after conversion. PR https://github.com/huugoo95/humami/pull/60 targets develop; all backend checks passed on initial candidate0cef663. Final frontend/container checks must pass before merge. No production deployment or protected-branch changes occurred.
