# Spec 032 — Make optimized images cacheable (phase 1)

## Why

Meal images on humami.es take 2–3.5 s to start arriving. Analysis on 2026-10-19 found several causes:

1. **Optimizer cache never written.** The frontend runtime image copies `.next` as root but runs as `node`. Every `/_next/image` write fails with `EACCES: mkdir '/app/.next/cache/images'` (reproduced with the production Dockerfile), so every request is an `X-Nextjs-Cache: MISS`. Each miss re-downloads the original from S3 in us-east-1 to the server in eu-west-3 and re-encodes it.
2. **Browsers revalidate every 60 s.** Optimized responses carry `Cache-Control: public, max-age=60` because the S3 objects have no cache headers and Next's default `minimumCacheTTL` is 60 s. Image keys contain a UUID, so their content never changes.
3. **Oversized downloads.** Meal, blog and detail images declare a fixed `width` without `sizes`. High-density screens download the 1920 px variant for cards that display at about 400 px.
4. **Late start for above-the-fold images.** Above-the-fold images are lazy and have no priority.
5. **Deprecated config.** `images.domains` is deprecated in favour of `remotePatterns`.

6. **Cache keys change on every request.** The backend presigns a new 1-hour S3 URL on every API call (`X-Amz-Date` changes every second). The Next cache key and the browser cache key include that URL, so without stable URLs neither cache would ever hit for real visitors (independent review of 1e7d457).

The cross-region bucket and fully stable public URLs (CloudFront, bucket in eu-west-3) remain phase 2, which needs infrastructure decisions.

## Scope

- Runtime image: give the `node` user ownership of `.next`, so the optimizer cache can be written.
- `next.config.mjs`:
  - `images.minimumCacheTTL` of 30 days.
  - `images.remotePatterns` restricted to `https://humami.s3.amazonaws.com/**`, replacing `domains`.
- Shared `sizes` values that match the layouts (meal grid of 1/2/3 columns, meal detail `max-w-5xl`, blog `max-w-4xl`), applied to every remote `next/image`.
- `priority` for the first three meal cards and for the meal detail and blog cover images.
- `images.maximumDiskCacheSize` of 256 MB, since the cache now persists in the container on the shared host disk.
- Backend: public image URLs (meal, blog cover, guide cover, about photo) come from a new `S3Service.getImageUrl`. It signs for 12 h and reuses the same URL for 6 h, so every visitor in that window shares cache keys. Private guide documents keep a fresh 1-hour URL per call (`getTempUrl`).

No API contract or storage change.

## Acceptance criteria

- Two API calls within 6 hours return the same image URL, while guide documents still get a fresh 1-hour URL.
- A repeated `/_next/image` request for the same URL is served from the optimizer cache (`X-Nextjs-Cache: HIT`) in the production image, with no `EACCES` in the logs.
- Optimized image responses allow browser caching for 30 days.
- Remote images declare `sizes`, so a 2x screen does not request the 1920 px variant for a meal card.
- Above-the-fold meal and cover images load with priority.
