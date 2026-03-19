# Recipe Ingestion Pipeline (v1)

Status: Active (iterative)
Owner: Hugo + tenacitas

## Goal
Standardize how recipes from external sources (books, PDFs, scans, notes) become high-quality Humami entries.

---

## End-to-end flow

### Phase 1 — Ingestion (source -> raw structured)
Input sources:
- recipe books
- scanned pages / photos
- PDFs / copied text

Output (raw structured draft):
- recipe title
- ingredients list (as-is)
- steps (as-is)
- optional: timing, servings, notes
- source metadata: book name + page + extraction date

**Rule:** do not publish directly from source text.

---

### Phase 2 — Normalization (raw -> Humami model)
Transform draft into Humami-compatible structure:
- map to `MealRequest` shape
- split correctly between `meal` and nested `recipes[]`
- standardize ingredient naming + units
- standardize instruction clarity
- normalize categories/tags if present

Output:
- validated payload ready for API submission

---

### Phase 3 — QA (editorial + technical)
Checks before publish:
- culinary coherence (ingredients/steps/timing)
- formatting consistency
- API contract validity
- duplicate detection (avoid near-identical entries)

Output:
- approved payload batch

---

### Phase 4 — Publish (API)
1. Create entry: `POST /api/meals` (JSON)
2. Optional image upload: `PUT /api/meals/{id}/image` (`multipart/form-data`, field `image`)
3. Save publication log (source -> created ID)

---

## Batch strategy
- Start with pilot batch: 10 recipes
- Review output quality + friction points
- Adjust rules
- Scale to larger batches (50+)

---

## Data contract checkpoint
For each published item, retain:
- source reference (book/page)
- normalized payload snapshot
- created meal ID
- publication timestamp

Use these references:
- `ops/recipe-import-template.json` (pipeline tracking)
- `ops/mealrequest-template.json` (payload ready for API)

---

## Iteration policy
This pipeline is versioned and expected to evolve.

When quality issues appear:
1. log issue type
2. add normalization or QA rule
3. run next batch with updated rule set
4. document change in `ops/decisions.md`

---

## Out of scope (v1)
- fully autonomous OCR-to-publish without human QA
- bulk public ingestion with zero review
- automatic SEO enrichment during ingestion
es (50+)

---

## Data contract checkpoint
For each published item, retain:
- source reference (book/page)
- normalized payload snapshot
- created meal ID
- publication timestamp

---

## Iteration policy
This pipeline is versioned and expected to evolve.

When quality issues appear:
1. log issue type
2. add normalization or QA rule
3. run next batch with updated rule set
4. document change in `ops/decisions.md`

---

## Out of scope (v1)
- fully autonomous OCR-to-publish without human QA
- bulk public ingestion with zero review
- automatic SEO enrichment during ingestion
