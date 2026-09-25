# Spec 024 — Technical guides and email-gated access

## Why

Humami can currently publish recipes and blog posts, but neither format fits a
longer technical resource such as *Tu primera pizza napolitana en casa*. That
13-page guide has a distinct outcome, asset type and user journey: a visitor
should understand its value publicly, leave an email to receive it, then read
it immediately in the browser and receive the PDF by email.

Putting it in the recipe catalogue would make the catalogue less useful, while
turning it into a blog post would lose the reusable access and delivery model
needed for future guides.

## Goal and priority

Priority: NEXT — establish a reusable technical-content channel and turn the
first pizza guide into a measurable lead magnet.

As a visitor interested in improving a cooking technique, I want to discover a
technical guide, see what it contains, provide my email once, and read it
without waiting for an inbox message.

Success metric: a visitor can discover a published guide, submit a valid email
and privacy acknowledgement, see the complete PDF in the same browser session,
and receive an email that restores the same access. The system records a
deduplicated guide request without exposing the guide document publicly.

## User story

As a home cook, I can obtain Humami's pizza guide with a low-friction form and
continue reading on the site immediately, while retaining the email as a
convenient way to return to it later.

## Scope

- Add a first-class **Guides** content type, separate from meals and blog
  posts, with published/draft status, SEO metadata, cover image, public
  preview fields and a private PDF asset.
- Add a public guide index at `/guias`, a public landing page at
  `/guias/[slug]`, and a reader at `/guias/[slug]/leer`.
- Make the landing page indexable and useful before capture: title, outcome,
  summary, what is included, technical facts, cover/preview and a clear CTA.
- Capture an email, a required acknowledgement of the privacy notice and an
  independent optional marketing opt-in. On success, establish browser access
  immediately and send a return-access email with the PDF download.
- Keep guide PDFs private in object storage. The reader and download obtain an
  expiring, authorized URL; no static public asset URL or storage key is
  returned by the public guide API.
- Provide authenticated creation/update/publish and asset upload operations so
  future guides can use the same flow.
- Add guide URLs and guide-specific metadata to discovery surfaces: navigation,
  sitemap, canonical URL and Open Graph/Twitter metadata.
- Launch the pizza guide through this model once its final PDF, cover and
  privacy-notice URL are supplied through the authenticated publishing flow.

## Non-goals

- No user account, password, paywall, subscription billing, newsletter
  campaign builder or audience segmentation.
- No conversion of the existing pizza PDF into HTML. The first reader embeds
  the protected PDF and provides an accessible download fallback; future
  interactive guide modules are a separate change.
- No change to meal ranking, recipe authoring, blog semantics or existing
  public assets.
- No commitment to a particular transactional-email vendor in source code. A
  configured delivery adapter and sender domain are required before launch.

## Decisions

1. **A guide landing page stays public; only the full document is gated.** It
   can be indexed, shared and assessed before email capture. This preserves
   discovery while making the long-form asset the lead magnet.
2. **Access is immediate.** A successful form submission creates a scoped,
   time-bounded browser access session and redirects to the reader. The email
   provides a separate return link; delivery does not block reading.
3. **Access is guide-scoped and revocable.** Opaque access credentials are
   stored only in hashed form, expire after a documented period, and can be
   reissued for the same email and guide without duplicating the lead.
4. **Marketing consent is optional and separate from delivery.** The form must
   not make marketing subscription a condition for receiving the guide.

## Risks and dependencies

The first PDF currently exists as an untracked local asset in `Claude outputs/`;
it must be supplied to the authenticated upload flow and must never be copied
to `humami-web/public/`. Sending a return email depends on a configured
transactional-email provider, sender identity and environment secrets. The
privacy notice URL and its version must be available before the public form
launches.

Public form endpoints can be abused or used to send unwanted messages. They
need request validation, rate limiting and a non-visible bot control. Expiring
URLs and a private bucket reduce sharing of a static link but cannot prevent a
reader from sharing a downloaded PDF; that is an accepted product limitation.

## Delivery

Implement on `feat/024-guides-email-access` from `develop`, validate through a
PR to `develop`, and do not deploy production as part of this change.
