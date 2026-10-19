---
name: humami-content-publish
description: Publish or update reviewed Humami meals, blog articles or About content through the existing API. Use only for requested remote content operations, not drafting or code deployment.
---

# Publish site content

1. Identify artifact, operation and intended visibility. There is only production (https://humami.es). Show Hugo the final text or payload and publish once he approves it; publishing is "ask first" per [AGENTS.md](../../../AGENTS.md).
2. Read only the selected contract: [meals](references/meals.md), [blog](references/blog.md) or [about](references/about.md). Revalidate the selected payload and, for meals, the reviewed image decision. Do not invent endpoints or assume a staging environment.
3. Write with [humami-api.sh](../../../scripts/humami-api.sh). It reads the write secret from the macOS Keychain (service `humami-write-secret`, account `humami`) and never prints it:
   - JSON: `scripts/humami-api.sh PATCH /api/about --json payload.json` (or `--json -` to read stdin).
   - Images: `scripts/humami-api.sh PUT /api/meals/<id>/image --file image=photo.jpg`.
   - It prints the response body and `HTTP <status>`, and exits non-zero unless the status is 2xx.
   - Write the payload to a file first (scratchpad for drafts, `ops/publications/` for records) and validate it.
   - If the Keychain has no secret, ask Hugo to add it with `security add-generic-password -U -a humami -s humami-write-secret -w`. Never ask for it in chat and never print, log or store it elsewhere.
   - Fallback without the Keychain (another machine): the **Publish content** workflow runs [publish-content.sh](../../../scripts/publish-content.sh) on the production host with the host's secret: `gh workflow run publish-content.yml -f method=PATCH -f path=/api/about -f payload="$(cat payload.json)"`. JSON only, no images.
4. Record source, local payload reference, destination, operation, timestamp, returned ID/slug and result in the task's publication record (redacted; use `ops/publications/` only for actual records). For meal images also record the delivered file, format, dimensions and checksum. Persist an ID as soon as creation returns, before image upload.
5. Verify the public read and relevant frontend URL for published content. A successful write is not proof the page rendered. Report partial results, including an absent image or unavailable verification.

## Failure handling
- A known created ID plus a failed image upload is a partial success: retry only the failed step using that ID, within authorization, after diagnosing the error. Never recreate the item.
- A timed-out creation with no ID is uncertain. Reconcile through available reads/returned identifiers; if unresolved, stop and report uncertainty. Do not blindly POST again: there is no server-side idempotency guarantee.
- No unbounded retries, guessed credentials, deletion compensation or production database edits. Do not mark a private draft as publicly verified when there is no draft-read endpoint.
