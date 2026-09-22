---
name: humami-content-publish
description: Publish or update reviewed Humami meals, blog articles or About content through the existing API. Use only for requested remote content operations, not drafting or code deployment.
---

# Publish site content

1. Identify artifact, operation, exact target environment and intended visibility. Present the concrete summary/payload for Hugo's review before real writes; apply existing explicit authorization if it already covers this reviewed artifact and destination. Otherwise obtain it at the write boundary. Skill discovery is not authorization.
2. Read only the selected contract: [meals](references/meals.md), [blog](references/blog.md) or [about](references/about.md). Revalidate the selected payload and image. Do not invent endpoints or assume a staging environment.
3. Use existing API tools with environment-provided credentials. Write endpoints require `X-HUMAMI-SECRET`; do not put its value in logs, tracked files, command text or reports. If tools/credentials are unavailable, retain the local artifact and report the blocker. No new client or connector is installed implicitly.
4. Record source, local payload reference, destination, operation, timestamp, returned ID/slug and result in the task's publication record (redacted; use `ops/publications/` only for actual records). Persist an ID as soon as creation returns, before image upload.
5. Verify the public read and relevant frontend URL for published content. A successful write is not proof the page rendered. Report partial results, including an absent image or unavailable verification.

## Failure handling
- A known created ID plus a failed image upload is a partial success: retry only the failed step using that ID, within authorization, after diagnosing the error. Never recreate the item.
- A timed-out creation with no ID is uncertain. Reconcile through available reads/returned identifiers; if unresolved, stop and report uncertainty. Do not blindly POST again: there is no server-side idempotency guarantee.
- No unbounded retries, guessed credentials, deletion compensation or production database edits. Do not mark a private draft as publicly verified when there is no draft-read endpoint.
