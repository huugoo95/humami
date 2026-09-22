# Spec 017 validation — 2026-09-22

## Implementation and contract
Quality eligibility and count run in MongoDB before paging. Blank queries use ID ascending; searches preserve relevance and use BSON-compatible Spring ID tie-breaking. Search offsets use long arithmetic to avoid overflow. Response fields and authentication are unchanged. No application dependency or frontend changes.

Catalogue source inspection confirms it uses response items and totalPages without sorting. Sitemap also follows totalPages; its requested limit=100 continues to be clamped to 48. No browser/UI execution is claimed.

## TDD evidence
- RED: MealPaginationIntegrationTest against temporary MongoDB 8.0.15 on loopback port 27177: 4 tests, 2 failures, 0 errors. The first page was empty instead of 12; equal-score results followed insertion order instead of ID order.
- GREEN: the same four MongoDB tests pass after the correction. Added three always-running unit tests and extended integration boundaries; all seven pass in the full suite.
- Tests include 25 eligible records with hidden records, counts and full pages, null/missing quality, threshold equality and zero, repeated ordering, mixed String/ObjectId IDs, search relevance, empty results, normalization and extreme page numbers.
- Integration tests require explicit `-Dhumami.test.mongoUri=mongodb://127.0.0.1:27177`; they create/drop uniquely named test databases. Without the property they are skipped, not evidence of MongoDB correctness.

## Commands and remaining gates
Use Java 21 (`JAVA_HOME=$(/usr/libexec/java_home -v 21)` on this host).

Full suite: `mvn -f humami-backend/pom.xml -Dhumami.test.mongoUri=mongodb://127.0.0.1:27177 verify` ran 42 tests, with 1 failure and 1 error: AboutControllerTest.shouldRejectImageUploadWithoutSecret expected 401 but returned 405; HumamiApplicationTests.contextLoads failed because the configured S3 access key is blank. No real S3 credentials were used.

Targeted `mvn verify` with `-Dtest=MealPaginationTest,MealPaginationIntegrationTest,MealServiceImplTest,MealControllerTest,MealWriteAuthInterceptorTest,WriteAuthInterceptorConfigTest` and the MongoDB property passed: 27 tests, 0 failures/errors/skips, backend packaged successfully. Baseline reproduction in a temporary archive of origin/develop dd6169c ran AboutControllerTest and HumamiApplicationTests: the same failure and error recur (6 tests total). These are pre-existing blockers; no unrelated fixes were included. No CI success or merge is claimed.

## Independent review
Temporary read-only reviewer review_017 reviewed the implementation and final test additions against the approved spec: no actionable findings; verified RED log and seven passing Surefire test results. Scope is the working diff based on definition commit 58e98b8. The delivery commit will preserve that code/test candidate. The comparator assumes normal Spring ID persistence; manually inserted hexadecimal BSON string IDs are not identifiable after mapping, with no evidence of such records.

OpenSpec strict validation and git diff --check passed. No data migration, live publication or deployment.

## Delivery
PR https://github.com/huugoo95/humami/pull/59 is open against develop. Candidate 7cafc409ff50bb12a3eb1c396feb38cd7dd16226 was independently matched to the reviewed code/tests. GitHub reports CLEAN but zero remote checks; the inspected branch protection requires no approval or status checks. The delivery skill still blocks integration without checks, and full-suite baseline failures remain documented. No merge was attempted. The isolated MongoDB test process was terminated after verification. No worktree was created.

## Approved blocker correction
User requested correcting the validation blockers. The About auth regression now sends PUT to the real endpoint and still verifies the service is not called. The full-context test supplies fictitious S3 keys scoped only to that test. No production configuration changed.

Full `mvn verify` with Java 21, local MongoDB URI and the integration-test property now passes: 42 tests, 0 failures/errors/skips, package successful. A backend GitHub Actions workflow runs this full verification with MongoDB 8.0.15 for PRs to develop/master and pushes to those branches. Remote execution is pending. Independent review of these additions found no actionable issues.
