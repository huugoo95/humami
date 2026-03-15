# Spec 009: data-model-v2-standardization

- **Status:** Draft
- **Owner:** Hugo + tenacitas
- **Created:** 2026-03-17

## 1) Problem

Current recipe data model is enough for MVP but has consistency and expressiveness gaps:

- `Ingredient.quantity` is integer-only (cannot represent 0.5, 1.25, etc.).
- `Meal.difficulty` is stored as free string while `DifficultyEnum` already exists.
- `Recipe.instructions` is plain `List<String>` (no step structure).
- No explicit optional-ingredient marker for toppings/finishing components.

These limitations reduce recipe quality and make UI standardization harder.

## 2) Goal and metric

- Goal: standardize core meal/recipe fields with minimal, non-infinite model evolution.
- Success metric:
  - New and edited recipes can represent decimal quantities.
  - Difficulty is enum-based and validated.
  - Instructions render from structured steps.
  - Optional ingredients are supported.

## 3) Scope

### In scope
1. `Ingredient.quantity`: Integer -> Decimal (`BigDecimal` preferred).
2. `Meal.difficulty`: String -> `DifficultyEnum`.
3. `Recipe.instructions`: `List<String>` -> `List<InstructionStep>`.
4. `Ingredient.isOptional`: add boolean flag (default `false`).
5. Adapt current recipes/doc payload examples to new contract.

### Out of scope
- API versioning (`v1`/`v2`) split.
- Unit enum expansion.
- Complex cooking metadata (temperature/timers per step beyond minimal structure).

## 4) User stories

- As a content editor, I want decimal ingredient amounts, so that recipes are accurate.
- As a user, I want standardized difficulty labels, so that recipe complexity is consistent.
- As a user, I want clearly structured instruction steps, so that preparation is easier to follow.
- As a user, I want optional ingredients identified, so that I can distinguish mandatory vs optional items.

## 5) Data model changes

### 5.1 Ingredient
Current:
- `name: String`
- `quantity: Integer`
- `unit: IngredientUnitEnum`
- `link: String`

Target:
- `name: String`
- `quantity: BigDecimal`
- `unit: IngredientUnitEnum`
- `link: String`
- `isOptional: boolean` (default `false`)

### 5.2 MealEntity
Current:
- `difficulty: String`

Target:
- `difficulty: DifficultyEnum`

### 5.3 Recipe
Current:
- `instructions: List<String>`

Target:
- `instructions: List<InstructionStep>`

New `InstructionStep` (minimal v1):
- `order: Integer`
- `text: String`

## 6) API contract impact

- `POST /api/meals` request payload and response must align with new types.
- Existing recipes should be adapted to maintain rendering and search consistency.
- No explicit versioning required in this iteration (agreed).

## 7) Migration approach (no version split)

1. Add new fields/types in backend domain + DTO + mapper.
2. Keep transitional parser compatibility where feasible (accepting old instruction shape during migration window).
3. Migrate existing records in DB:
   - integer quantity -> decimal quantity (same numeric value)
   - string difficulty -> enum mapping
   - instruction strings -> `{order, text}`
   - `isOptional` default false
4. Remove temporary compatibility path once data migration is complete.

## 8) Acceptance criteria (Given / When / Then)

1. Given ingredient amount `0.5`
   When meal is created
   Then quantity is persisted and returned as decimal.

2. Given an invalid difficulty string
   When meal is created/updated
   Then request is rejected with validation error.

3. Given structured instruction steps
   When meal detail is retrieved
   Then steps are returned in order with text.

4. Given ingredient without `isOptional`
   When persisted/retrieved
   Then `isOptional` is `false` by default.

5. Given previously stored recipes
   When migration is complete
   Then they remain readable and conform to new schema.

## 9) Risks and mitigation

- Risk: front/backend contract mismatch during transition.
  - Mitigation: coordinated rollout + temporary compatibility parser.

- Risk: migration script errors on legacy records.
  - Mitigation: dry-run on backup/staging first.

- Risk: enum mapping failures for old difficulty values.
  - Mitigation: explicit mapping table + fallback report.

## 10) Rollout and rollback

- Rollout:
  1) deploy schema-compatible backend,
  2) run migration,
  3) deploy frontend aligned to new contract,
  4) verify sample meal pages and creation flow.

- Rollback:
  - restore DB backup,
  - revert to previous backend/frontend commits.

## 11) Open questions

- None for this scope (unit enum expansion explicitly postponed).
