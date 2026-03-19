# Runbook: Recipe Publishing

## Goal
Publish meal/recipe content without breaking API contract or semantics.

## Preconditions
- Payload reviewed and validated
- Explicit approval from Hugo before real write calls

## Flow
1. Prepare payload (`MealRequest`) in Spanish content fields.
2. Validate structure:
   - Meal represents final composition
   - `recipes[]` split by real preparation blocks
   - enums/timings/servings coherent
3. Preview summary for approval.
4. Create meal:
   - `POST /api/meals` (JSON)
5. Optional image upload:
   - `PUT /api/meals/{id}/image` (`multipart/form-data`, field `image`)
6. Verify meal detail response and image URL behavior.

## Validation checklist
- Contract fields are valid
- No mixed unrelated preparations in one recipe block
- Response is readable from FE perspective

## References
- `docs/RECIPES.md`
- `docs/recipe-ingestion-pipeline.md`
- `ops/recipe-import-template.json`
- `memory/humami-contracts.md` (operational contract memory)
