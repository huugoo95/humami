## 1. Definition

- [x] Define phase 2 scope, design, rollout, rollback, cost and manual AWS steps (this change).
- [ ] Owner approves the definition, the bucket name `humami-media-eu` and the subdomain `img.humami.es`.

## 2. AWS setup (owner; manual steps M1–M10 in design.md)

- [ ] M1 Media bucket in eu-west-3 (BPA on, ACLs disabled, SSE-S3, versioning + noncurrent expiry).
- [ ] M2–M3 ACM certificate for `img.humami.es` in us-east-1, DNS-validated, status *Issued*.
- [ ] M4–M6 OAC, CloudFront distribution and bucket policy scoped to the distribution.
- [ ] M7 DNS record `img.humami.es` → distribution.
- [ ] M8 IAM policy of the backend user extended to the media bucket.
- [ ] M9 Inventory recorded (object count, size, extensions) and initial copy run, excluding `private/*`.
- [ ] M10 CDN, direct-access 403, HTTPS redirect and no-`private/` checks recorded.

## 3. Backend (TDD)

- [ ] RED: tests for public URL format and encoding, determinism, `private/` guard, legacy-mode fallback, upload bucket and headers, delete bucket, and an unchanged 1-hour guide PDF presign.
- [ ] Storage configuration properties (media/private bucket, region, public base URL), with the hard-coded bucket and unused properties removed.
- [ ] Regional S3 clients; public-mode `getImageUrl` without signing; uploads with immutable `Cache-Control`; deletes on the media bucket.
- [ ] Compose and `.env` documentation for `HUMAMI_STORAGE_MEDIA_*` (no values committed).

## 4. Frontend (TDD)

- [ ] RED: `next.config` test allows `img.humami.es`; metadata tests for an absolute CDN `og:image` and the blog post metadata.
- [ ] `remotePatterns` adds `img.humami.es` (keeps the S3 host for now).
- [ ] Blog post `generateMetadata` with the cover `og:image` / `twitter:image` and the brand fallback; `og:image` dimensions for CDN images.

## 5. Tooling

- [ ] `scripts/verify-media-urls.sh`: reads a key list, HEAD-checks each CDN URL for status, `Content-Type` and `Cache-Control`, and exits non-zero on failures.

## 6. Delivery and rollout

- [ ] Independent review; PR to develop; release deployed with public mode **disabled**; health checks pass.
- [ ] Cutover (owner-authorized): pause uploads, final delta copy, set `.env`, recreate the backend, run the verification script.
- [ ] Production evidence: stable URLs across more than 6 h, optimizer MISS ≤ 1.0 s, CDN hit, `og:image` 200 after more than 12 h, guide PDF still presigned 1 h, new upload has the immutable headers in the media bucket.
- [ ] After 30 days without rollback: remove `humami.s3.amazonaws.com` from `remotePatterns`; propose the legacy-bucket cleanup (M13) separately.

## Execution log (2026-10-19)

- [x] Legacy bucket `humami`: Block Public Access enabled. Verified no anonymous access (403) and that the site still works.
- [x] Created `humami-media-eu` and `humami-private-eu` in eu-west-3 (BPA, BucketOwnerEnforced, SSE-S3, tags).
- [x] Initial copy with `scripts/migrate-media-eu.sh`: 28 media objects (6.1 MB, with immutable Cache-Control) and 1 PDF. Re-run reports `copied=0 skipped=29`.
- [x] Backend and frontend implementation: backend 58 tests (S3ServiceImplTest 8) and frontend 14 tests pass; lint and build OK.
