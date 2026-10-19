## 1. Definition

- [x] Define phase 2 scope, design, rollout, rollback, cost and manual AWS steps (this change).
- [x] Owner approves the definition, the bucket name `humami-media-eu` and the subdomain `img.humami.es`.

## 2. AWS setup (owner; manual steps M1–M10 in design.md)

- [x] M1 Media bucket in eu-west-3 (BPA on, ACLs disabled, SSE-S3). Versioning deferred (see follow-ups).
- [x] M2–M3 ACM certificate for `img.humami.es` in us-east-1, DNS-validated, status *Issued*.
- [x] M4–M6 OAC, CloudFront distribution and bucket policy scoped to the distribution.
- [x] M7 DNS record `img.humami.es` → distribution.
- [x] M8 IAM policy of the backend user extended to the media bucket.
- [x] M9 Inventory recorded (object count, size, extensions) and initial copy run, excluding `private/*`.
- [x] M10 CDN, direct-access 403, HTTPS redirect and no-`private/` checks recorded.

## 3. Backend (TDD)

- [x] RED: tests for public URL format and encoding, determinism, `private/` guard, legacy-mode fallback, upload bucket and headers, delete bucket, and an unchanged 1-hour guide PDF presign.
- [x] Storage configuration properties (media/private bucket, region, public base URL), with the hard-coded bucket and unused properties removed.
- [x] Regional S3 clients; public-mode `getImageUrl` without signing; uploads with immutable `Cache-Control`; deletes on the media bucket.
- [x] No `.env` change needed: production defaults live in `application.properties`, and `HUMAMI_STORAGE_*` remain optional overrides.

## 4. Frontend (TDD)

- [x] RED: `next.config` test allows `img.humami.es`; metadata tests for an absolute CDN `og:image` and the blog post metadata.
- [x] `remotePatterns` allows `img.humami.es` and the media bucket host (presigned fallback). The legacy S3 host is dropped, because rollback is an image rollback (see design).
- [x] Blog post `generateMetadata` with the cover `og:image` / `twitter:image` and the brand fallback.
- [ ] Follow-up: `og:image` dimensions for CDN images (uploads are normalized to 1600×900, legacy images may differ).

## 5. Tooling

- [x] Replaced by `scripts/migrate-media-eu.sh` (idempotent copy) plus a recorded check of every bucket object and every API-referenced key via the CDN. Original item: `scripts/verify-media-urls.sh`: reads a key list, HEAD-checks each CDN URL for status, `Content-Type` and `Cache-Control`, and exits non-zero on failures.

## 6. Delivery and rollout

- [x] Two independent reviews (f74a301 and a6d3007) with no blocker; PR #91 merged to develop (a76b5d7); release 0.6.0 PR #92 merged to master (18eb524) and deployed by run 39275742412 on 2026-10-19. Smoke checks passed; 8084 MB free on the host.
- [x] Cutover: `scripts/migrate-media-eu.sh` ran before and after the deploy and found no new keys (copied=0, skipped=29 both times).
- [ ] Production evidence: stable URLs across more than 6 h, optimizer MISS ≤ 1.0 s, CDN hit, `og:image` 200 after more than 12 h, guide PDF still presigned 1 h, new upload has the immutable headers in the media bucket.
- [ ] After 30 days without rollback: remove the media bucket presigned host from `remotePatterns` if unused, and propose the legacy-bucket cleanup (M13) separately.

## Execution log (2026-10-19)

- [x] Legacy bucket `humami`: Block Public Access enabled. Verified no anonymous access (403) and that the site still works.
- [x] Created `humami-media-eu` and `humami-private-eu` in eu-west-3 (BPA, BucketOwnerEnforced, SSE-S3, tags).
- [x] Initial copy with `scripts/migrate-media-eu.sh`: 28 media objects (6.1 MB, with immutable Cache-Control) and 1 PDF. Re-run reports `copied=0 skipped=29`.
- [x] Backend and frontend implementation: backend 58 tests (S3ServiceImplTest 8) and frontend 14 tests pass; lint and build OK.
- [x] ACM certificate for `img.humami.es` (us-east-1), validated by a DNS record in Route 53. Status: ISSUED.
- [x] CloudFront:
  - Distribution E3ROFQV56V0LD4 (`d3rw49ruee2cjh.cloudfront.net`) with OAC E1YB2D1TQ94J2V: PriceClass_100, HTTP/2+3, IPv6, redirect to HTTPS, CachingOptimized and SecurityHeadersPolicy, alias `img.humami.es`, TLSv1.2_2021 with SNI.
  - Bucket policy grants GetObject only to this distribution; the policy status is not public.
- [x] Route 53: A and AAAA alias records from `img.humami.es` to the distribution.
- [x] IAM: `humami-s3` policy v3 allows List, Get, Put and Delete on `humami`, `humami-media-eu` and `humami-private-eu`; checked with an IAM simulation.
- [x] CDN checks:
  - All 28 media objects and all 9 keys referenced by the public API return 200 via `img.humami.es`, with `Cache-Control: public, max-age=31536000, immutable` and `x-cache: Hit from cloudfront` on repeat.
  - Direct S3 access returns 403; `private/` through the CDN returns 403; HTTP redirects to HTTPS (301).

## Production evidence (0.6.0, 2026-10-19)

- `/`, `/meals`, `/api/meals`, `/guias/pizza-napolitana-desde-cero` and `/blog` return 200.
- The API returns stable `https://img.humami.es/<key>` URLs, identical across calls and with no query string.
- A CloudFront Hit returns in 0.09 s.
- Meal pages declare `og:image` with the CDN URL.
- Next optimizer for a new variant: MISS in 2.47 s, then HIT in 0.29 s. The origin fetch is now about 0.1 s, so the remaining MISS time is image processing CPU on the t4g.small host (follow-up).
- Not verified live:
  - Blog `og:image`: there are no published posts.
  - Guide PDF presign from `humami-private-eu`: it needs the email access flow. Unit tests cover it.
