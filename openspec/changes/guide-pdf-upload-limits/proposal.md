# Spec 027 — Permit guide PDF uploads

## Why

The first production guide PDF is 5.48 MB. Production Nginx and Spring Boot use their default 1 MB multipart limits, so its private document upload returns HTTP 413.

## Scope

Set a 10 MB file limit in Spring Boot, with an 11 MB multipart request/proxy allowance for form overhead in the HTTPS production Nginx server. The limit applies to authenticated uploads, including private guide PDFs. No change to public document access or storage policy.

## Acceptance criteria

- A PDF below 10 MB can reach the authenticated guide document endpoint through production Nginx.
- Spring Boot accepts the same request size.
- Nginx continues to proxy API requests and preserve TLS behavior.
