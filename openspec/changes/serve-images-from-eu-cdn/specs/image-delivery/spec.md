# image-delivery

## ADDED Requirements

### Requirement: Stable public image URLs

When a public media base URL is configured, the backend SHALL return each public image (meal image, blog cover, guide cover, about photo) as `<public base URL>/<key>` with each path segment percent-encoded, with no query string and no signature, and the same key SHALL always produce the same URL. Stored values that are already absolute `http(s)` URLs SHALL be returned unchanged.

#### Scenario: Same URL over time

- **WHEN** a meal with image key `Tarta_de_queso_<uuid>.jpg` is fetched now and again more than 6 hours later
- **THEN** both responses contain `https://img.humami.es/Tarta_de_queso_<uuid>.jpg`

#### Scenario: Non-ASCII key

- **WHEN** a meal's image key is `Ñoquis_<uuid>.jpg`
- **THEN** the returned URL is `https://img.humami.es/%C3%91oquis_<uuid>.jpg`

#### Scenario: Legacy mode without a public base URL

- **WHEN** no public media base URL is configured
- **THEN** image URLs are presigned as in phase 1, so removing the configuration rolls the behaviour back

### Requirement: Private guide documents stay private

Guide PDFs SHALL remain in the private bucket and SHALL only be delivered through fresh presigned URLs valid for 1 hour. The backend MUST NOT build a public URL for any key under `private/`, and the public media bucket MUST NOT contain objects under `private/`.

#### Scenario: Guide document request

- **WHEN** an authorized visitor requests a guide's document
- **THEN** the response is a presigned URL for the private bucket that expires after 1 hour

#### Scenario: Private key passed to the public URL builder

- **WHEN** the backend is asked for a public image URL for a key starting with `private/`
- **THEN** it fails instead of returning a URL

### Requirement: Public media bucket served only through CloudFront

Public images SHALL be stored in a dedicated private bucket in eu-west-3. They SHALL be readable only through a CloudFront distribution that uses Origin Access Control, on `img.humami.es` with an ACM certificate, and HTTP SHALL be redirected to HTTPS.

#### Scenario: Request through the CDN

- **WHEN** `https://img.humami.es/<existing key>` is requested twice
- **THEN** both responses are `200`, and the second is served from the CloudFront cache

#### Scenario: Direct bucket access

- **WHEN** the same key is requested from the bucket's S3 endpoint without credentials
- **THEN** S3 responds `403`

### Requirement: Immutable cache headers on public images

Every public image object, migrated or newly uploaded, SHALL carry `Content-Type` matching its format and `Cache-Control: public, max-age=31536000, immutable`, and image keys SHALL never be reused for different content.

#### Scenario: New upload

- **WHEN** an image is uploaded for a meal, blog post, guide or the about page
- **THEN** the object is written to the public media bucket under a new UUID key with the immutable `Cache-Control` header

#### Scenario: Migrated object

- **WHEN** the migration verification checks every image key referenced in Mongo
- **THEN** each key returns `200` from `img.humami.es` with the immutable `Cache-Control` header, and none is missing

### Requirement: Frontend accepts CDN images

The frontend image optimizer SHALL accept remote images from `https://img.humami.es` and SHALL NOT allow arbitrary remote hosts.

#### Scenario: Optimized CDN image

- **WHEN** `/_next/image` is requested for an `https://img.humami.es` image URL
- **THEN** the optimizer serves the image instead of rejecting the host

### Requirement: Stable Open Graph images

Meal, guide and blog post pages SHALL declare `og:image` and `twitter:image` as absolute, stable URLs: the public CDN URL of their image, or the brand social image when they have none.

#### Scenario: Shared meal link re-scraped later

- **WHEN** a crawler fetches the `og:image` of a meal page more than 12 hours after rendering it
- **THEN** the image URL responds `200`

#### Scenario: Blog post with a cover

- **WHEN** the HTML of a blog post with a cover image is fetched
- **THEN** it contains an `og:image` pointing to the cover's `https://img.humami.es` URL
