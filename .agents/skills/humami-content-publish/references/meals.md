# Meal operations

For schema/semantics consult [meal contract](../../../../docs/RECIPES.md). Before publication require the authoring checks, including the two meaningful steps that the script does not enforce.

- Create: `POST /api/meals`, JSON MealRequest. Persist returned ID immediately.
- Image: `PUT /api/meals/{id}/image`, multipart field `image`, after creation succeeds.
- Verify: `GET /api/meals/{id}` and `/meals/{id}`, including the returned image URL when supplied.
- Current meals have no draft/publication state: creation writes directly to the live collection. A draft must remain local until its target write is authorized.
- For updates inspect the current [controller](../../../../humami-backend/src/main/java/com/hugo/humami/controller/MealController.java) and mapper before using PATCH; do not assume arbitrary partial payloads are safe.

All writes require the shared authentication described by the parent skill. The public API does not guarantee idempotent creation or expose a reliable unique source key.
