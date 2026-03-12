# Meal / Recipe Model

## Objective
Align product and engineering semantics for `Meal` and `Recipe` in Humami.

## Core concepts

### Meal
A **Meal** is the final dish/composition a user wants to cook/eat (e.g. kebab, ramen, pizza).

### Recipe
A **Recipe** is one concrete preparation process (steps + ingredients) that can belong to a meal.

Examples:
- Kebab sauce
- Kebab bread
- Ramen broth
- Chashu
- Aromatic oil

## Modeling rules

### 1) Default rule: one main recipe per meal
Most meals should have **one main recipe** representing the core preparation.

### 2) Embedded multiple recipes when tightly coupled
A meal can contain **multiple recipes** when the user naturally prepares them together as one cooking flow.

Examples:
- Kebab meal with:
  - main kebab prep
  - kebab sauce recipe
- Ramen meal with:
  - broth
  - chashu
  - aromatic oil

### 3) Reference external recipes when optional/reusable
If a preparation is reusable across many meals or optional for the flow, prefer **reference** over always embedding.

Examples:
- Generic yogurt sauce used in many meals
- Generic flatbread recipe reused by multiple dishes

## Practical guideline (decision matrix)

For each additional preparation, ask:
1. Is it normally cooked only for this meal?
2. Is it required for expected quality of this meal?
3. Would separating it make the user flow worse?

- If mostly **yes** -> include as additional `Recipe` inside the `Meal`.
- If mostly **no** -> keep meal lean and reference a reusable recipe.

## Product intent
- Keep meals easy to understand (avoid over-fragmenting).
- Preserve flexibility for advanced dishes with sub-preparations.
- Balance UX simplicity with culinary realism.

## Current implementation note
Current schema supports `Meal` with `recipes[]`, which already fits this model.
A future iteration may add explicit `referencedRecipeIds[]` if cross-meal reuse needs first-class support.
