# Design

## Current state

Humami has independent meal and blog models. `BlogPostEntity` contains public
editorial fields only and `blog_posts` has no protected asset, lead or access
lifecycle. The frontend has `/blog` and `/meals` routes but no guides route.
The sitemap enumerates meals and static pages only. Existing S3 support can
upload images and issue temporary GET URLs, but it normalizes every upload as
a 16:9 JPEG and therefore cannot receive a PDF safely. There is no subscriber
model, outbound email adapter, rate limiter or public write endpoint.

## Content and persistence model

Use a separate `guides` MongoDB collection rather than adding a type field to
`blog_posts`. A guide has a distinct protected document and access lifecycle;
sharing the generic SEO fields in code is preferable to prematurely combining
the persistence models.

`Guide` SHALL contain at least:

- id, slug, title, excerpt and public landing-page body/sections;
- cover image key/URL, preview image keys and private PDF object key;
- practical facts such as estimated duration, level and page count;
- SEO title/description, status, publishedAt, createdAt and updatedAt.

Only published guides are returned by public read APIs. Public responses never
contain the PDF object key, access credential, lead counts or unpublished
content.

Use a `guide_leads` collection keyed by normalized email plus guide id. It
contains the delivery acknowledgement timestamp, privacy-notice version,
optional marketing consent and timestamp, source, delivery state and audit
timestamps. Repeated valid requests update/reissue access for that guide
instead of creating duplicate leads.

Use a `guide_access_tokens` collection for opaque, random credentials. Store a
secure hash, guide id, lead id, expiry, redemption/revocation metadata and no
recoverable token value. The browser session and email-return link are both
derived from a valid guide-specific token.

## API and access contract

### Public discovery

```
GET /api/guides
GET /api/guides/{slug}
```

The list contains published guide cards. The detail response contains the
public landing data and access state only; it never exposes private document
storage details.

### Public access request

```
POST /api/guides/{slug}/access
```

The request accepts `email`, `privacyNoticeVersion`, optional
`marketingOptIn`, a source identifier and a honeypot field. It validates
format and acknowledgement, applies per-IP and per-email rate limits, and
does not disclose whether an address was already present. A success response
sets or establishes a secure guide-scoped browser session and gives the
frontend the canonical reader URL. It queues/sends the return email through a
delivery port.

The external email link redeems a token, establishes the same browser session,
removes the token from the visible URL and sends the visitor to
`/guias/{slug}/leer`.

### Authorized document access

```
GET /api/guides/{slug}/document
```

The endpoint requires a valid guide-scoped session. It creates a short-lived
private-object URL suitable for inline PDF viewing and downloading. It returns
an authorization failure for missing, expired, revoked or cross-guide access.
The frontend reader presents the PDF inline and offers a download action using
that authorized URL, with a readable failure/re-request state.

### Content operations

```
POST/PATCH /api/guides
POST /api/guides/{id}/cover
POST /api/guides/{id}/document
```

These operations use the existing write-auth convention. PDF upload is a
separate storage method from image normalization: it validates a PDF MIME type
and size limit, writes to a non-public prefix and records only its object key.
Replacing or unpublishing a guide revokes future document issuance; storage
cleanup semantics are explicit and tested.

## Frontend design

`/guias` presents published guides as a new public content section. The main
navigation includes **Guías** without removing existing routes. Each landing
page has a guide label, outcome-led title, cover, facts, contents/preview and
one form CTA. It has its own canonical and social metadata, and published
guide landing URLs enter the sitemap.

After a successful form submission, the UI moves directly to `/leer` in the
same tab. The reader validates access server-side or through its data layer,
then embeds the authorized PDF. It must not persist an access token in
localStorage or render an object-storage URL in public markup. A link in the
email restores the reader session on another device.

The form explains the delivery purpose, links the current privacy notice and
offers an unchecked optional marketing checkbox. It includes loading,
validation, duplicate-request and rate-limit-safe states without revealing
whether a specific email already exists.

## Email delivery boundary

Introduce an application-level `GuideAccessEmailSender` port with a provider
adapter selected by configuration. Tests use a fake adapter. Production launch
requires a configured sender, return URL base, provider credentials and a
tested sender domain; no provider secret enters source control. The email
contains the guide title, short return-access link, PDF download availability
and the standard unsubscribe route only when marketing has been opted into.

## Security and operational decisions

- Extend the existing write-auth interceptor only to editor endpoints. Public
  discovery and access-request endpoints intentionally remain public and use
  their own validation, bot control and rate limits.
- Apply strict file validation and an explicit documented PDF size limit.
- Configure private storage separately from image delivery; never use the
  image normalizer for PDFs.
- Record consent evidence minimally and avoid logging raw email addresses or
  credentials.
- Make all access expiry, storage prefix, sender identity and privacy-notice
  URL configurable environment values with safe production validation.

## Verification and rollback

Backend tests cover published visibility, request validation, duplicate lead
handling, token hashing/expiry/revocation, cross-guide denial, document URL
issuance and fake email delivery. Frontend tests cover public guide rendering,
form states, immediate reader transition and metadata. End-to-end checks cover
a local fake sender and private-object adapter, not real recipient addresses.

Run backend tests, frontend lint/tests/production build, and visual checks at
desktop and 375 px for index, landing, form success and reader fallback.
Verify sitemap and rendered metadata for the pizza guide. Rollback removes the
navigation and guide routes, disables the public access endpoint and revokes
issued guide tokens; no meal or blog data changes are involved.
