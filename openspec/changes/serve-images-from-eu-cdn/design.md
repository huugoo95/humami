# Design — Spec 034: image delivery phase 2

## Current state (develop at f4900e6, production 0.5.4)

- `S3ServiceImpl` hard-codes `bucketName = "humami"`. `aws.s3.bucket-name` in `application.properties` and `AWS_S3_BUCKET_NAME` in Compose are not read. One `S3Client` and one `S3Presigner` are built for `aws.s3.region` (us-east-1 in production).
- `getImageUrl(key)` presigns for 12 h and reuses the URL for 6 h. Callers: `MealServiceImpl` (meal image), `BlogPostServiceImpl` (cover), `GuideServiceImpl` (cover), `AboutServiceImpl.resolvePhotoUrl` (passes absolute `http(s)` values through unchanged).
- `getTempUrl(key)` presigns for 1 h. Its only caller is `GuideServiceImpl` for `documentKey` (`private/guides/...pdf`).
- `uploadImage` normalizes to a 1600×900 JPEG (quality 0.8), writes `<name>_<uuid>.jpg` with `Content-Type: image/jpeg` and no `Cache-Control`. `deleteImage` deletes from the same bucket. Callers: meal, blog, guide cover and about photo uploads; meal delete.
- Mongo stores keys only: `meals.image`, `blog_posts.coverImage`, `guides.coverImage`, `about_pages.photoUrl` (key or absolute URL), and `guides.documentKey` (private).
- `next.config.mjs` allows only `https://humami.s3.amazonaws.com/**`. `/our-story` renders the about photo with a plain `<img>`; meal, blog and guide images use `next/image`.
- `mealMetadata` and `guideMetadata` put the API image URL (presigned) in `og:image` / `twitter:image`. `app/blog/[slug]/page.tsx` has no `generateMetadata`. `metadataBase` is `https://humami.es`.

## Target architecture

```
Browser ──> humami.es (nginx ─> Next) ──/_next/image──> img.humami.es (CloudFront, edge in Europe)
   │                                                        │ OAC (SigV4)
   └──── og:image / <img> ─────────────> img.humami.es ─────┴──> s3://humami-media-eu (eu-west-3, private)

Backend ──PutObject/DeleteObject (IAM user)──> s3://humami-media-eu
Backend ──presign 1 h──> s3://humami (us-east-1) private/guides/*.pdf   (unchanged)
```

### 1. Media bucket

- Name: `humami-media-eu` (S3 names are global; if it is taken, the owner picks another one and sets it in configuration). Region `eu-west-3`.
- Block Public Access: all four settings on. Object Ownership: bucket owner enforced (ACLs disabled). Default encryption SSE-S3. No static website hosting.
- Versioning: enabled, with a lifecycle rule that expires noncurrent versions after 30 days. This guards against accidental deletes during the migration window at negligible cost.
- Content rule: **only public images**. Nothing under `private/` is ever written there. The backend refuses to build a public URL for a key that starts with `private/`, and fails fast.
- Bucket policy: CloudFront only, scoped to this distribution.

```json
{
  "Version": "2012-10-17",
  "Statement": [{
    "Sid": "AllowCloudFrontReadViaOAC",
    "Effect": "Allow",
    "Principal": { "Service": "cloudfront.amazonaws.com" },
    "Action": "s3:GetObject",
    "Resource": "arn:aws:s3:::humami-media-eu/*",
    "Condition": { "StringEquals": { "AWS:SourceArn": "arn:aws:cloudfront::<ACCOUNT_ID>:distribution/<DISTRIBUTION_ID>" } }
  }]
}
```

Without `s3:ListBucket`, a missing key returns `403` through CloudFront rather than `404`. That is acceptable and hides the bucket listing.

### 2. Key layout and migration

Keys stay identical (`<name>_<uuid>.jpg` at the bucket root), so no Mongo migration is needed. The legacy bucket keeps its objects until a later cleanup.

**Inventory** (owner, read-only): list the legacy bucket, group by top-level prefix and extension, and record the object count and total size in the tasks evidence:

```bash
aws s3 ls s3://humami --recursive --summarize
```

**Copy** (owner, AWS CloudShell or an admin profile; run once per image extension present, with the matching `Content-Type`):

```bash
aws s3 sync s3://humami s3://humami-media-eu \
  --source-region us-east-1 --region eu-west-3 \
  --exclude "*" --include "*.jpg" --include "*.jpeg" --exclude "private/*" \
  --metadata-directive REPLACE \
  --content-type image/jpeg \
  --cache-control "public, max-age=31536000, immutable"
```

`--metadata-directive REPLACE` is needed so S3 writes the new `Cache-Control`; it also drops other user metadata, which Humami does not use. `sync` is idempotent, so the same command is the final delta copy at cutover.

**Post-copy checks:**

- `aws s3 ls s3://humami-media-eu/private/` returns nothing.
- `aws s3api head-object` on a sample shows `CacheControl` and `ContentType` as expected.
- The key list exported from Mongo (see below) is checked by `scripts/verify-media-urls.sh`: it HEAD-requests `https://img.humami.es/<encoded key>` for each key, prints missing or wrong-header keys, and exits non-zero on any failure.

Export of referenced keys (owner, `mongosh` against production; read-only):

```js
const keys = [
  ...db.meals.distinct("image"),
  ...db.blog_posts.distinct("coverImage"),
  ...db.guides.distinct("coverImage"),
  ...db.about_pages.distinct("photoUrl"),
].filter(k => k && !/^https?:\/\//.test(k));
print(keys.join("\n"));
```

### 3. CloudFront

- Origin: the bucket's regional REST endpoint `humami-media-eu.s3.eu-west-3.amazonaws.com`, with an Origin Access Control (S3, sign requests: always, SigV4). No legacy OAI and no website endpoint.
- Default behaviour: GET/HEAD only. Viewer protocol: redirect HTTP to HTTPS. Compress objects automatically: on (harmless for JPEG). Cache policy: managed `CachingOptimized` (no query strings, cookies or headers in the key; max TTL 1 year, honours the origin `Cache-Control`). Origin request policy: none. Response headers policy: managed `SecurityHeadersPolicy` (adds `X-Content-Type-Options: nosniff` and similar).
- HTTP/2 and HTTP/3 on; IPv6 on. Price class: "Use only North America and Europe" (`PriceClass_100`), which is the cheapest class and fully covers Spanish traffic.
- Alternate domain name `img.humami.es`. Certificate: public ACM certificate for `img.humami.es`, requested in **us-east-1** (CloudFront only accepts certificates from that region), DNS-validated. Security policy `TLSv1.2_2021`, SNI only.
- Standard logging: off. Enabling it later costs S3 storage only.
- Origin Shield, Lambda@Edge, CloudFront Functions, WAF: not used.
- DNS: `img.humami.es` CNAME to `dxxxxxxxx.cloudfront.net` at the `humami.es` DNS provider (an ALIAS record if the zone is in Route 53).

Invalidations are not part of normal operation, because keys are immutable. A one-off `/*` invalidation is free within the first 1,000 paths per month.

### 4. Backend

Configuration (Spring properties, overridable by environment; secrets stay in `.env`):

| Property | Production value | Default when unset |
|---|---|---|
| `humami.storage.private.bucket` | `humami` | `humami` |
| `humami.storage.private.region` | `us-east-1` | `aws.s3.region` |
| `humami.storage.media.bucket` | `humami-media-eu` | private bucket |
| `humami.storage.media.region` | `eu-west-3` | private region |
| `humami.storage.media.public-base-url` | `https://img.humami.es` | empty → legacy mode |

Compose passes `HUMAMI_STORAGE_MEDIA_BUCKET`, `HUMAMI_STORAGE_MEDIA_REGION` and `HUMAMI_STORAGE_MEDIA_PUBLIC_BASE_URL` from `.env`. The existing access key/secret pair is reused, with an extended IAM policy. The unused `aws.s3.bucket-name` property and `S3_BUCKET` mapping are removed, or replaced by the new properties, so configuration has a single source of truth.

Behaviour:

- **Public mode** (`public-base-url` set):
  - `getImageUrl(key)` returns `publicBaseUrl + "/" + encodePath(key)`. Each segment is percent-encoded per RFC 3986 (UTF-8), with `/` preserved and no trailing slash duplication.
  - It is pure and deterministic. The phase 1 signing cache (`imageUrls`, `SignedUrl`, 6 h reuse) is deleted in public mode.
  - Keys starting with `private/` throw `IllegalArgumentException`.
- **Legacy mode** (`public-base-url` empty): the phase 1 behaviour against the media bucket configuration. With no media settings, that is the legacy bucket, so a rollback is "unset three variables".
- `uploadImage` writes to the media bucket with `Content-Type: image/jpeg` and `Cache-Control: public, max-age=31536000, immutable`. Key generation is unchanged and always contains a fresh UUID; keys are never overwritten.
- `deleteImage` deletes from the media bucket. Orphans in the legacy bucket are left for the cleanup.
- `getTempUrl` and `uploadGuideDocument` use the private bucket and private region only; behaviour unchanged.
- S3 clients: one `S3Client` for the media region and one `S3Client` + `S3Presigner` for the private region. Separate regional clients avoid SDK v2 `301 PermanentRedirect` errors and keep each bucket's region explicit. When both regions are equal, a single client may be shared.
- No API contract change: the response fields stay `String` URLs; only their form changes. `AboutServiceImpl` keeps passing absolute URLs through.

Tests (TDD, `S3ServiceImplTest` and config tests): public URL format and encoding (ASCII, `Ñ`, spaces already replaced, `+`, `%`), determinism over time, `private/` guard, legacy-mode fallback, upload request bucket and headers, delete bucket, guide PDF presign still 1 h on the private bucket.

### 5. Frontend

- `next.config.mjs` `images.remotePatterns`: add `{ protocol: "https", hostname: "img.humami.es", pathname: "/**" }`. Keep the `humami.s3.amazonaws.com` entry until the rollback window ends, then remove it in a follow-up commit within this change (final task). The hostname is a literal because `images` configuration is fixed at build time.
- `minimumCacheTTL` (30 days) and `maximumDiskCacheSize` (256 MB) are unchanged. With stable URLs, the optimizer cache entry for a key and width now lives until eviction or the next deploy, instead of 6 hours.
- `/our-story` keeps its plain `<img>`; it now loads directly from CloudFront.
- Test (`next.config` test from phase 1): the CDN host is allowed and no wildcard host is added.

### 6. Decision: keep the Next optimizer (no pre-generated variants)

| Option | Benefit | Cost |
|---|---|---|
| **A. Keep the Next optimizer** (chosen) | No new code or dependency; AVIF/WebP negotiation and the phase 1 `sizes` keep working; the origin fetch drops from ~0.5–0.7 s cross-region to a CloudFront edge in Europe | CPU encode on the shared EC2 host on each miss; the optimizer cache is lost on every deploy |
| B. Pre-generate variants at upload (e.g. 640/828/1080/1600 × WebP) + custom loader to CloudFront | Zero encode on the host; every hit served from the edge | Java ImageIO has no WebP/AVIF writer, so a new dependency or native libvips is needed (approved-libraries policy); a backfill job for existing images; 4–8× the objects; a loader contract that must match `sizes` |
| C. CloudFront-side resizing (Lambda@Edge or AWS Dynamic Image Transformation) | Edge variants without host CPU | More infrastructure and moving parts, cost per transform, overkill for current traffic |

Rationale: after phase 1, the remaining latency comes mostly from the cross-region origin and the 6-hour URL rotation, which this change removes. Encoding cost is paid once per image, width and format per deploy. Option B is revisited in its own spec only if, after 034, the measured optimizer MISS stays above 1.0 s, or host CPU from `/_next/image` becomes a problem. A cheaper intermediate step is persisting `.next/cache/images` in a named volume, which is also a separate change, given the disk-space history in spec 033.

### 7. Open Graph

- Meals and guides: `mealMetadata` / `guideMetadata` already use the API image URL. In public mode that URL is the stable `https://img.humami.es/...`, so no logic change is needed. Tests assert that an absolute CDN URL passes through unchanged, and that the brand fallback still applies when there is no image.
- Blog posts: add `generateMetadata` to `app/blog/[slug]/page.tsx`: title, description (excerpt), canonical, and `og:image` / `twitter:image` from `coverImage`, or the brand image as fallback. It follows `mealMetadata` and adds a `blogPostMetadata` helper in `lib/brandMetadata.ts`.
- `og:image:width` / `og:image:height` of 1600/900 are declared only when the image comes from the CDN, since every upload is normalized to 1600×900. This lets crawlers render previews without fetching the image first.
- After cutover, the owner can re-scrape a few key URLs in the Facebook Sharing Debugger; others refresh naturally.

## Rollout

Order matters: infrastructure and data first, code with public mode disabled next, and the configuration flip last.

1. **AWS setup** (owner, manual steps M1–M8 below). No production impact.
2. **Initial copy and verification** (M9–M10) against `https://img.humami.es`.
3. **Ship code** through the normal flow (PR to develop, release). Production `.env` does not yet set `HUMAMI_STORAGE_MEDIA_PUBLIC_BASE_URL`, so behaviour is phase 1 (legacy mode); the frontend already allows both hosts. Deploy and run the standard health checks.
4. **Cutover** (owner-authorized window, about 15 minutes):
   1. Pause content publishing (no uploads).
   2. Run the final delta `sync` (M9 again).
   3. Set the three `HUMAMI_STORAGE_MEDIA_*` variables in the production `.env`.
   4. Recreate only the backend container.
   5. Verify: API image URLs, `verify-media-urls.sh`, a `/_next/image` MISS→HIT timing, `og:image` on one meal, guide and blog page, and a guide PDF download.
5. **Observe** for 7 days: optimizer MISS timing, error logs, a test upload of a new meal image (it lands in the media bucket with the cache headers).
6. **Close the rollback window** after 30 days without rollback: remove the S3 host from `remotePatterns`, and propose the legacy-bucket cleanup (deleting non-`private/` objects) as a separate, owner-confirmed operation.

## Rollback

- **Before cutover**: nothing to undo; the code runs in legacy mode.
- **After cutover**:
  1. Copy any images uploaded after cutover back to the legacy bucket: `aws s3 sync s3://humami-media-eu s3://humami --source-region eu-west-3 --region us-east-1`. This is safe because the media bucket holds no `private/` keys and keys are unique.
  2. Unset the three variables and recreate the backend. URLs revert to presigned URLs from `humami`. The frontend still allows the S3 host, so no frontend rollback is needed.
- **Code rollback**: redeploy the previous release. The phase 1 code reads the legacy bucket, which still has every pre-cutover image; step 1 covers newer ones.
- CloudFront and the bucket can stay in place after a rollback; their idle cost is about $0.

## Cost estimate (October 2026 list prices; verify in the console at creation)

Assumptions: under 1 GB of public images (to be confirmed by the inventory) and under 50 GB/month of image egress.

| Item | Estimate |
|---|---|
| S3 Standard storage eu-west-3 (~$0.024/GB-month) | < $0.03/month |
| Noncurrent versions (30-day expiry) | ≈ $0 |
| One-off copy us-east-1 → eu-west-3 (inter-region $0.02/GB + COPY requests) | < $0.05 once |
| CloudFront egress and requests | $0 within the always-free tier (1 TB egress and 10 M requests per month); above that ~$0.085/GB in Europe |
| S3 → CloudFront origin transfer | $0 (no charge); GET requests ~$0.0004/1,000 |
| ACM public certificate | $0 |
| DNS | $0 extra (Route 53 hosted zone $0.50/month only if the zone is moved there, which is not required) |
| Savings | No more inter-region transfer for originals fetched by the Paris host from us-east-1 |

Expected total: **$0–1/month**, plus a negligible one-off copy. Recommended guardrail: an AWS Budget alert at $5/month on the account, if one does not already exist.

## Manual AWS steps (the owner performs or authorizes each one)

Agents do not create AWS resources or hold AWS credentials for this change. Each step records evidence (IDs, command output without secrets) in `tasks.md`.

| # | Step | Where | Reversible |
|---|---|---|---|
| M1 | Create bucket `humami-media-eu` in eu-west-3: BPA all on, ACLs disabled, SSE-S3, versioning on, lifecycle "expire noncurrent versions after 30 days" | S3 console | Yes (delete empty bucket) |
| M2 | Request a public ACM certificate for `img.humami.es` in **us-east-1**, DNS validation | ACM console (N. Virginia) | Yes |
| M3 | Add the ACM validation CNAME at the `humami.es` DNS provider; wait for status *Issued* | DNS provider | Yes |
| M4 | Create the OAC (origin type S3, always sign, SigV4) | CloudFront console | Yes |
| M5 | Create the CloudFront distribution with the settings in §3 (origin = media bucket REST endpoint + OAC, `CachingOptimized`, `SecurityHeadersPolicy`, redirect to HTTPS, `PriceClass_100`, CNAME `img.humami.es`, ACM certificate) | CloudFront console | Yes (disable, then delete) |
| M6 | Apply the bucket policy from §1 with the real account and distribution IDs | S3 console | Yes |
| M7 | Create the DNS record `img.humami.es` → distribution domain | DNS provider | Yes |
| M8 | Extend the backend IAM user's policy with `s3:PutObject`, `s3:GetObject` and `s3:DeleteObject` on `arn:aws:s3:::humami-media-eu/*`. Keep the existing `humami` permissions. Do not create new access keys | IAM console | Yes |
| M9 | Run the inventory, then the copy from §2 (CloudShell). Repeat the copy as the final delta at cutover | CloudShell | Yes (objects can be deleted) |
| M10 | Verify: `curl -sI https://img.humami.es/<sample key>` (200, cache headers, then `Hit from cloudfront`); `curl -sI https://humami-media-eu.s3.eu-west-3.amazonaws.com/<sample key>` (403); `http://` redirects; no `private/` objects | Terminal | — |
| M11 | At cutover: add `HUMAMI_STORAGE_MEDIA_BUCKET`, `HUMAMI_STORAGE_MEDIA_REGION` and `HUMAMI_STORAGE_MEDIA_PUBLIC_BASE_URL` to the production `.env` (not committed) and authorize recreating the backend container | EC2 host | Yes (unset + recreate) |
| M12 | (Optional) AWS Budget alert at $5/month | Billing console | Yes |
| M13 | (After 30 days, separate confirmation) Delete non-`private/` objects from the legacy bucket | S3 | **No**, unless versioning is on in `humami` |

## Security notes

- The bucket remains private; only this distribution can read it (OAC plus `AWS:SourceArn`).
- Private guide PDFs never leave the legacy bucket; the copy excludes `private/*`, which is verified after the copy, and the backend refuses to publish `private/` keys.
- No credentials in the repository, specs, logs or PR; IDs such as the account or distribution ID go only in the live bucket policy.
- The public image URLs expose nothing new: the same images are already publicly reachable through presigned URLs and `/_next/image`.
