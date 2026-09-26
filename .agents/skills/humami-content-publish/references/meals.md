# Meal operations

For schema/semantics consult [meal contract](../../../../docs/RECIPES.md). Before publication require the authoring checks, including the two meaningful steps that the script does not enforce.

- Create: `POST /api/meals`, JSON MealRequest. Persist returned ID immediately.
- Update: inspect the current controller and mapper before using `PATCH`; send a complete reviewed payload when the mapper is not proven safe for arbitrary partial payloads.
- Image: `PUT /api/meals/{id}/image`, multipart field `image`, after a create or content update succeeds.
- Verify: `GET /api/meals/{id}` and `/meals/{id}`, including the returned image URL when supplied.
- Current meals have no draft/publication state: creation writes directly to the live collection. A draft must remain local until its target write is authorized.

## Meal image gate

A meal must have a reviewed image decision before publication: a ready image, an approved edit, or an explicit user decision to keep the existing image. Do not silently publish a new meal without resolving that decision.

Before upload, inspect the file and keep a local copy. Use a web-ready JPEG or PNG with the dish clearly visible, a crop suitable for the recipe page, and a conservative file size. If the API returns `413 Payload Too Large`, optimize or resize the same approved image, then retry **only** the image upload once; do not repeat meal creation or the content PATCH. Record the delivered file's format, dimensions and checksum with the publication result.

All writes require the shared authentication described by the parent skill. The public API does not guarantee idempotent creation or expose a reliable unique source key.
