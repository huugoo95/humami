# RECIPES.md — Recipe and API Operations

## Objective
Standardize how meals/recipes are created in Humami without contract errors.

## Base contract (current)
- Create meal/recipe: `POST /api/meals` with JSON (`MealRequest`).
- Upload image: `PUT /api/meals/{id}/image` with `multipart/form-data`, field `image`.

> Operational reference: `memory/humami-contracts.md` (tenacitas workspace).

## Recommended flow
1. Prepare and validate payload.
2. Execute creation (`POST /api/meals`).
3. If needed, upload image (`PUT /api/meals/{id}/image`).
4. Verify response and register result.

## Pre-create validation checklist (Meal)
Before every real write, validate explicitly:

1. **Meal vs Recipe semantics**
   - `Meal` represents the final dish/composition.
   - `recipes[]` contains concrete preparations (no mixed sub-preps in a single recipe).

2. **Preparation separation**
   - Ingredients and steps are separated per preparation.
   - Do not mix sauce/bread/broth/chashu/etc. instructions in one recipe when they are distinct blocks.

3. **Enums and required fields**
   - `type`, `difficulty`, `servings`, timings, and other contract fields.
   - Enum values are valid and coherent with the use case.

4. **Culinary consistency**
   - If multiple recipes exist, confirm they are a natural part of the same meal flow (e.g., kebab + sauce, ramen + broth/chashu/oil).
   - If a prep is optional or globally reusable, evaluate reference instead of embedding.

5. **Preview + human approval**
   - Show payload summary first.
   - Request Hugo's explicit final approval before `POST /api/meals`.

## Operational safety rule
Before executing real API writes from chat, request explicit validation from Hugo.

## Language convention
- Technical docs/specs and API contracts: **English**.
- Recipe content data (`name`, `description`, `instructions`, FAQs): **Spanish** (for now).

## Pending
- Add real payload examples and expected responses.
