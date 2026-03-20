# Meal Authoring Playbook (single path)

Status: Active
Owner: Hugo + tenacitas

## Objective
Create Humami meals from external recipe inputs using one consistent, non-improvised workflow.

## Golden rule
If payload does not pass preflight validation, do not publish.

---

## One-path workflow

### 1) Parse input
Input can be book, screenshot, PDF, notes, etc.
Extract only culinary facts first:
- meal title
- recipe blocks
- ingredients
- instructions
- timings
- servings

### 2) Normalize to `MealRequest`
Transform into API-ready payload (`POST /api/meals`).
Use `ops/mealrequest-template.json`.

### 3) Preflight validation (mandatory)
Run:

```bash
python3 scripts/validate-mealrequest.py path/to/payload.json
```

Validation checks:
- required shape
- allowed enums (`difficulty`, `type`, `ingredients[].unit`)
- minimum content constraints (recipes/instructions/servings)

### 4) Human QA
Quick review before publish:
- culinary coherence
- clean language and readability
- duplicate check

### 5) Publish
- Create meal:

```bash
curl -X POST https://humami.es/api/meals \
  -H 'Content-Type: application/json' \
  -d @payload.json
```

- Optional image upload:

```bash
curl -X PUT "https://humami.es/api/meals/<MEAL_ID>/image" \
  -F "image=@/path/to/photo.jpg"
```

### 6) Log publication
Store source + payload + created ID in the batch log.

---

## Canonical references
- `ops/mealrequest-template.json` (payload template)
- `scripts/validate-mealrequest.py` (preflight validator)
- `docs/recipe-ingestion-pipeline.md` (broader ingestion process)

---

## Definition of done
A meal is done only when:
1. payload passed validator,
2. meal created successfully,
3. image uploaded if applicable,
4. publication trace logged.
