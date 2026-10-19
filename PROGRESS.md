# Project Progress

_Last updated: 2026-10-19_

## Current state
- Production runs on `master`. Trunk-based delivery with automatic deploy and rollback since 2026-10-19 (see `ops/decisions.md`).
- 0.6.0 (spec 034): images served from `img.humami.es` (CloudFront, bucket `humami-media-eu` in eu-west-3); guide PDFs in `humami-private-eu`.
- Specs 029–033 shipped: guide access button, frontend audit gate, deploy script path, image cache, disk-space hotfix.

## Pending owner decisions
- Delete the legacy `humami` bucket (us-east-1) after the rollback window.
- Delete the old dev MongoDB volume on the host and the Atlas `humami_dev` database.
- January 2027: the t4g.small free trial ends (Savings Plan, Lightsail or another VPS).

## Follow-ups
- Image variants generated at upload to remove optimizer CPU load on the host.
- Media bucket versioning; tighter IAM once the legacy bucket is gone.
- `og:image` dimensions for CDN images.
