## ADDED Requirements

### Requirement: Published technical guides are a distinct public content type

The system SHALL store guides separately from meals and blog posts and SHALL
expose only published guides through the public list and detail APIs.

#### Scenario: Visitor opens the guide catalogue

- **WHEN** at least one guide is published and the visitor requests `/guias`
- **THEN** the visitor sees cards for the published guides without draft or
  private document information.

#### Scenario: Visitor requests a draft guide

- **WHEN** the visitor requests the public route or API for a draft or unknown
  guide slug
- **THEN** the system returns the same not-found behaviour used for unavailable
  public content.

### Requirement: A guide has an indexable public landing page

The system SHALL render every published guide at `/guias/[slug]` with a
publicly useful summary, technical facts, preview/cover and a guide-access CTA.
It SHALL provide guide-specific canonical, Open Graph and Twitter metadata and
include the landing URL in the sitemap.

#### Scenario: Visitor shares the pizza guide landing page

- **WHEN** a crawler requests the published pizza guide URL
- **THEN** it receives a canonical URL and guide-specific title, description
  and social image metadata without requiring access to the PDF.

### Requirement: Email capture gives immediate reader access

The system SHALL accept a valid guide-access request only after a privacy
notice acknowledgement and SHALL establish reader access in the same browser
session without waiting for email delivery. Marketing consent SHALL be optional
and independent from delivery access.

#### Scenario: Visitor submits the access form successfully

- **WHEN** a visitor submits a valid email, the current privacy-notice version
  and a clear bot-control field for a published guide
- **THEN** the system records or refreshes one lead for that email and guide,
  establishes guide-scoped access, and takes the visitor to `/guias/[slug]/leer`.

#### Scenario: Visitor omits the privacy acknowledgement

- **WHEN** a visitor submits the form without the required acknowledgement
- **THEN** the system rejects the request with a clear validation message and
  does not issue reader access.

### Requirement: Guide return email restores access without exposing a public asset

The system SHALL send a guide return-access email after a successful request.
The email link SHALL redeem a time-bounded, guide-scoped credential, remove it
from the visible URL and restore reader access. No public API, HTML page or
email SHALL disclose a permanent storage key or unrestricted PDF URL.

#### Scenario: Visitor opens the return email on another device

- **WHEN** the visitor follows a valid, unexpired return link
- **THEN** the system establishes access to that guide and redirects to its
  reader without retaining the token in the browser URL.

#### Scenario: Credential cannot access another guide

- **WHEN** a credential issued for one guide is presented for a different
  guide or after expiry/revocation
- **THEN** the system denies document access and offers a safe way to request
  access again.

### Requirement: Protected PDFs are viewed and downloaded through authorized access

The system SHALL store guide PDFs outside public static assets and SHALL issue
short-lived document URLs only to an authorized reader session. The reader
SHALL display the PDF in the website and provide a download fallback.

#### Scenario: Authorized visitor opens the reader

- **WHEN** a visitor with valid guide access requests `/guias/[slug]/leer`
- **THEN** the page shows the guide PDF inline and a download action using an
  authorized temporary URL.

#### Scenario: Unauthorized visitor opens the reader

- **WHEN** a visitor without valid guide access requests `/guias/[slug]/leer`
- **THEN** the page returns the visitor to the guide landing page with a
  non-sensitive prompt to request access.

### Requirement: Editors can manage guide assets without publishing them publicly

The system SHALL require existing editor write authentication to create, edit,
publish and upload guide assets. It SHALL validate PDF uploads independently
from image uploads and store their objects under a private guide prefix.

#### Scenario: Editor uploads a guide PDF

- **WHEN** an authenticated editor uploads a valid PDF within the configured
  size limit for a guide
- **THEN** the system stores it privately, records its key on that guide and
  does not make it retrievable through a static public URL.

#### Scenario: Unauthenticated caller attempts to publish a guide

- **WHEN** a caller without editor authentication uses a guide content endpoint
- **THEN** the system rejects the request and leaves guide content unchanged.

### Requirement: Public access capture resists routine abuse

The system SHALL validate form input, apply per-IP and per-email throttling,
and use a non-visible bot control before sending guide access emails. It SHALL
not reveal whether a submitted email already has a lead for that guide.

#### Scenario: Same email asks for the same guide again

- **WHEN** a valid repeated request is within allowed throttling limits
- **THEN** the system refreshes/reissues access and presents the same generic
  success response without creating a duplicate lead or exposing prior state.

#### Scenario: Caller exceeds access-request rate limits

- **WHEN** a caller exceeds the configured rate limit
- **THEN** the system does not send another email or issue another access token
  and returns a generic retry-later response.
