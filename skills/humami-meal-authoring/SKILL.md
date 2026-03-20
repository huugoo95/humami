---
name: humami-meal-authoring
description: Create and publish Humami meals from external recipe inputs using one strict path: normalize to MealRequest, run preflight enum/schema validation, and publish only if validation passes. Use when ingesting recipes from books/PDFs/photos/notes into Humami.
---

# Humami Meal Authoring

Use this skill to avoid improvisation when creating meals.

## Required flow
1. Normalize source input to `MealRequest` using `ops/mealrequest-template.json`.
2. Run preflight validator:
   - `python3 scripts/validate-mealrequest.py <payload.json>`
3. If validator fails: stop and fix payload.
4. If validator passes: publish via `POST /api/meals`.
5. If image exists: upload via `PUT /api/meals/{id}/image` with `image` multipart field.
6. Log source -> payload -> created meal ID.

## References
- `docs/meal-authoring-playbook.md`
- `docs/recipe-ingestion-pipeline.md`
- `ops/mealrequest-template.json`
- `ops/recipe-import-template.json`
