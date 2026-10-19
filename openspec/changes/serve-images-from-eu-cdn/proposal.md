# Spec 034 — Serve public images from an EU bucket through CloudFront (image delivery phase 2)

## Why

Phase 1 ([spec 032](../fix-image-delivery-cache/proposal.md), released in 0.5.4) made the Next image optimizer cache work. Production measurements after phase 1: an optimizer MISS takes about 2.7 s, a HIT about 0.17 s, and browsers reuse images between visits. Three limits remain, all caused by where the originals live and how they are addressed:

1. **URLs still rotate.** `S3ServiceImpl.getImageUrl` presigns 12-hour URLs and reuses them for 6 hours. Every 6 hours each image gets a new URL, so the optimizer cache key and the browser cache key change: one optimizer MISS per image and width per window, and returning visitors download images again.
2. **Every miss crosses the Atlantic.** The bucket `humami` is in us-east-1, while the server runs in eu-west-3 (Paris). Each optimizer miss fetches the original cross-region (about 0.5–0.7 s TTFB of the 2.7 s miss) and pays inter-region transfer.
3. **Social previews break.** Meal and guide `og:image` / `twitter:image` values are presigned URLs. Crawlers that fetch them after 12 hours get `403 AccessDenied`, so shared links on WhatsApp, Facebook or X can lose their image. Blog posts declare no `og:image` at all.

The bucket cannot simply be made public, because it also holds private guide PDFs under `private/guides/`. Buckets cannot change region, and the name `humami` cannot be reused while the old bucket exists.

User impact: slower first image paint on meal and blog pages after each 6-hour window and after each deploy, extra downloads for returning visitors, and missing images in shared links.

## Goal, success metric and priority

Goal: every public image (meal, blog cover, guide cover, about photo) has one stable, cacheable HTTPS URL served from Europe, while guide PDFs stay private.

Success metrics, measured in production after cutover:

- The API returns the same image URL for a given key indefinitely (no `X-Amz-*` parameters) — verified across a period longer than 6 hours.
- An optimizer MISS for a meal card takes ≤ 1.0 s (baseline 2.7 s), because the original is fetched from CloudFront in Europe instead of S3 in us-east-1.
- A direct request to `https://img.humami.es/<key>` returns `200` with `Cache-Control: public, max-age=31536000, immutable`, and a repeat request is `X-Cache: Hit from cloudfront`.
- The `og:image` of a meal page still returns `200` more than 12 hours after the page was rendered.

Priority: NOW-adjacent operational quality. It does not change the product offer, but image speed affects every recipe page and shared links feed the content pipeline (a NOW priority in `docs/HUMAMI.md`). Cost impact is around $0–1/month (see design).

## User story

As a visitor opening a Humami recipe, or a link someone shared, I want the photo to appear quickly and reliably, so that the page feels fast and the shared preview shows the dish.

As the owner, I want public images and private guide PDFs stored separately, so that serving images publicly can never expose a paid guide.

## Scope

- **Private documents move to Europe too** (decision 2026-10-19): guide PDFs go to a second private bucket `humami-private-eu` in eu-west-3 with the same keys, so nothing depends on us-east-1 and the legacy bucket can be retired.
- **New public media bucket** in eu-west-3 (proposed name `humami-media-eu`), private with Block Public Access, holding only public images.
- **Migration** of existing public image objects from `humami` to the new bucket with the same keys and `Cache-Control: public, max-age=31536000, immutable`; Mongo documents keep their keys unchanged.
- **CloudFront distribution** with Origin Access Control (OAC) on `img.humami.es`, TLS certificate from ACM in us-east-1.
- **Backend**:
  - Public image URLs become `https://img.humami.es/<url-encoded key>`, built from configuration, with no presigning.
  - New uploads go to the media bucket with long-lived cache headers.
  - Image deletes target the media bucket.
  - Guide PDFs stay in `humami` under `private/guides/` with fresh 1-hour presigned URLs.
  - Storage settings (bucket names, regions, public base URL) become configuration instead of the hard-coded `"humami"`.
- **Frontend**: `images.remotePatterns` allows `img.humami.es`; the S3 host stays allowed during the rollback window and is removed afterwards.
- **Open Graph**: meal, guide and blog post `og:image` / `twitter:image` use the stable absolute URL. Blog posts gain an `og:image` from their cover.
- **Decision on the optimizer**: keep the Next image optimizer in this phase (see design); pre-generating variants at upload is a follow-up only if the metrics require it.
- **Rollout, rollback, cost estimate and manual AWS steps** that the owner performs or authorizes.

## Non-goals

- Changing how private guide PDFs are delivered (still fresh 1-hour presigned URLs, now from `humami-private-eu`).
- Deleting the old bucket or its image objects (a later cleanup, after the rollback window).
- Pre-generating image variants, a custom Next image loader, or CloudFront-side resizing.
- Persisting the optimizer cache across deploys (a Docker volume); a possible follow-up.
- Signed CloudFront URLs or hotlink protection: these images are public content.
- Infrastructure as code (Terraform/CDK): Humami has no IaC today; the AWS steps are documented and executed manually.
- Dev environment image storage: dev keeps `dev-storage-disabled`. It may optionally read the same public CDN, but that is not required here.
- Creating AWS resources as part of this definition.

## Acceptance criteria

- **Given** a meal with image key `K` in Mongo, **when** the meal is fetched twice, days apart, **then** both responses contain `https://img.humami.es/K` (path-encoded) with no query string.
- **Given** a guide with a PDF, **when** its document is requested, **then** the response is a presigned URL for bucket `humami` in us-east-1 valid for 1 hour, as today.
- **Given** a new image uploaded through any upload endpoint, **when** the object is inspected, **then** it is in the media bucket with `Content-Type: image/jpeg` and `Cache-Control: public, max-age=31536000, immutable`.
- **Given** the media bucket, **when** anyone requests an object directly from its S3 endpoint, **then** S3 answers `403`; only CloudFront can read it.
- **Given** the CloudFront distribution, **when** `https://img.humami.es/<existing key>` is requested twice, **then** both answers are `200` with the long-lived `Cache-Control` and the second is a CloudFront cache hit; HTTP redirects to HTTPS.
- **Given** a key under `private/`, **when** it is requested through `img.humami.es`, **then** the answer is `403` or `404`, because the media bucket contains no private objects.
- **Given** a meal, guide or blog post page with an image, **when** its HTML is fetched, **then** `og:image` and `twitter:image` are absolute `https://img.humami.es/...` URLs that return `200`.
- **Given** the migration has run, **when** the verification script checks every image key referenced in Mongo, **then** each one returns `200` from `img.humami.es` with the expected headers, and the script reports zero missing keys.
- **Given** the backend runs with no public base URL configured, **then** it falls back to the phase 1 behaviour (presigned URLs from the legacy bucket), so rollback is a configuration change.

## Edge cases

- Keys with non-ASCII characters or reserved URL characters (meal names such as "Ñoquis" or "Crème brûlée" produce keys like `Ñoquis_<uuid>.jpg`) are percent-encoded per path segment; `/` separators are preserved.
- Stored values that are already absolute `http(s)://` URLs (allowed today for the about photo) are returned unchanged.
- Empty or missing keys produce no image URL, as today.
- An image uploaded to the legacy bucket between the initial copy and cutover is picked up by the final delta copy.
- An image uploaded to the media bucket after cutover and before a rollback must be copied back to the legacy bucket before rolling back.
- A meal image replaced with a new upload keeps the old object as an orphan (existing behaviour); immutable caching is safe because keys are never overwritten.
- A crawler that cached an old presigned `og:image` keeps the broken URL until it re-scrapes the page; sharing debuggers can force a refresh.

## Risks and dependencies

- **Manual AWS work** by the owner (bucket, IAM, OAC, ACM, CloudFront, DNS). Mitigation: a step-by-step runbook with verification commands in the design.
- **DNS provider access** for `humami.es` (an ACM validation CNAME and a CNAME for `img`).
- **ACM validation and CloudFront propagation** can take from minutes to about an hour; this blocks only the rollout, not the code.
- **Immutable caching of a wrong object**: if an object were overwritten under the same key, browsers and CloudFront would serve the old one for up to a year. Mitigated by UUID keys and a rule that image keys are never reused; a CloudFront invalidation clears the CDN, but not browsers.
- **IAM permissions**: the backend's IAM user needs write and delete access to the new bucket; missing permissions would make uploads fail after cutover. Verified before cutover.
- **Accidental public exposure of private PDFs** if `private/` objects were copied to the media bucket. Mitigated by excluding `private/*` in the copy, a post-copy check, and a backend guard against public URLs for `private/` keys.
- Depends on phase 1 (spec 032) being in production, which it is (0.5.4).
