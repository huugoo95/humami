# Spec 015: recipe-quality-score-and-search-threshold

- **Status:** Draft
- **Owner:** Hugo + tenacitas
- **Created:** 2026-03-28

## 1) Problem
Humami needs a minimum quality bar for recipes so that low-value entries do not degrade the public search experience.

Right now, recipes can vary significantly in usefulness:
- some have clear ingredients, quantities, steps, and images
- others are too vague, incomplete, or not practical enough for real use

Without a measurable score, there is no systematic way to:
- filter weak recipes from public search
- identify which recipes need editorial improvement
- improve overall catalog quality over time

## 2) Goal and metric
Introduce a recipe quality score from `0.0` to `1.0` that is calculated automatically whenever a recipe is created or modified.

This score should allow Humami to:
- filter recipes below a minimum threshold in search
- expose quality gaps for editorial improvement
- progressively raise overall catalog usefulness

Success metrics:
1. Every recipe has a calculated quality score after create/update flows.
2. Search can filter recipes using a minimum quality threshold (e.g. `0.5`).
3. The score is explainable via sub-scores and/or flags.
4. Existing low-quality recipes can be identified and improved over time.

## 3) Scope
### In scope
- define a `qualityScore` from `0.0` to `1.0`
- define the initial heuristic model for computing the score
- calculate the score on recipe create/update/image upload flows
- store the score and supporting metadata in backend persistence
- expose score/filter support in API/search behavior
- support backfill/recalculation for existing recipes

### Out of scope
- LLM-based judging as the primary scoring engine
- full editorial workflow UI
- moderation policy beyond recipe usefulness/completeness scoring
- complex semantic food knowledge or taste scoring

## 4) Product direction
The score should reflect whether a recipe is sufficiently complete and specific to be useful to a real human trying to cook it.

This is not a prestige score.
It is primarily a **utility score**.

Humami should use the score to:
- hide weak recipes from public search by default
- surface improvement opportunities internally
- preserve incomplete recipes without deleting them

## 5) Functional requirements
### FR1 — Automatic score generation on create
Given a new recipe is created,
when persistence completes,
then the backend must compute and store a `qualityScore`.

### FR2 — Automatic score generation on update
Given an existing recipe is modified,
when recipe content changes,
then the backend must recompute and store the `qualityScore`.

### FR3 — Score refresh on image upload
Given a recipe image is uploaded or replaced,
when the image association is persisted,
then the backend must recompute the score so media-related quality can change.

### FR4 — Search threshold filtering
Given recipe search/list endpoints,
when a minimum quality threshold is supplied or enabled by default,
then only recipes with `qualityScore >= threshold` should be returned.

### FR5 — Explainability
Given a stored score,
when the system exposes recipe quality metadata,
then it must be possible to explain the score using sub-scores and/or flags.

### FR6 — Recalculation support
Given the quality formula evolves,
when a recalculation job is run,
then existing recipes must be re-scoreable in bulk.

## 6) Proposed scoring model
Use a deterministic heuristic score from `0.0` to `1.0` composed of sub-scores.

### 6.1 Final score
Initial formula:

```text
qualityScore =
  0.55 * completenessScore +
  0.30 * specificityScore +
  0.15 * mediaScore
```

The formula should be versioned.

### 6.2 Sub-score A — completenessScore
Measures whether the recipe contains enough structure to be usable.

Suggested signals:
- recipe name present and non-trivial
- description present and non-trivial
- ingredients present
- steps/instructions present
- time metadata present
- servings present

Suggested internal weighting:
- name: 0.10
- description: 0.10
- ingredients: 0.25
- steps: 0.25
- time metadata: 0.15
- servings: 0.15

### 6.3 Sub-score B — specificityScore
Measures whether the recipe is concrete rather than vague.

Suggested positive signals:
- percentage of ingredients with numeric quantity
- percentage of ingredients with recognized unit
- steps with action-oriented language
- time references inside instructions
- temperature references where applicable

Suggested negative signals:
- overuse of vague phrases such as:
  - "al gusto"
  - "un poco"
  - "lo suficiente"
  - "hasta que esté"
- too-short instructions
- overly generic ingredient naming

### 6.4 Sub-score C — mediaScore
Measures whether the recipe includes useful media.

Suggested signals:
- primary image present
- optional extension later for multiple useful images

This score should have lower weight than structural and specificity signals.

## 7) Data model proposal
Each recipe should store quality metadata similar to:

```json
{
  "qualityScore": 0.67,
  "qualityVersion": 1,
  "qualityBreakdown": {
    "completeness": 0.72,
    "specificity": 0.58,
    "media": 1.0
  },
  "qualityFlags": [
    "missing-servings",
    "missing-time-metadata",
    "ingredients-missing-units"
  ]
}
```

## 8) Search behavior
### Public default behavior
Public-facing search should support a minimum threshold such as:
- `minQualityScore=0.5`

Recommended first default:
- hide recipes with `qualityScore < 0.5` from public search/list views

### Internal/editorial behavior
Internal/admin/editorial flows may omit the threshold so weak recipes remain visible for improvement.

## 9) Acceptance criteria
1. Given a recipe is created,
   when the create flow completes,
   then a persisted `qualityScore` exists.

2. Given a recipe is updated,
   when fields affecting usefulness change,
   then `qualityScore` is recalculated.

3. Given a recipe image is added,
   when the upload flow completes,
   then media-related quality is recalculated.

4. Given search is called with `minQualityScore=0.5`,
   when low-quality recipes exist,
   then recipes below `0.5` are excluded.

5. Given a stored recipe,
   when quality metadata is inspected,
   then at least a score, formula version, and breakdown/flags are available.

6. Given the scoring rules change,
   when a backfill/recompute is run,
   then legacy recipes receive refreshed scores.

## 10) Technical design
### 10.1 Where scoring happens
Primary recommendation:
- compute score in backend service layer
- trigger on:
  - create meal
  - update meal
  - image upload

### 10.2 Why backend-owned
Backend ownership ensures:
- consistent score calculation
- one source of truth
- reusable search filtering
- no dependency on client-side implementation

### 10.3 Recompute trigger points
Recommended trigger points:
- `POST /api/meals`
- update/edit recipe endpoint(s)
- `PUT /api/meals/{id}/image`
- optional admin backfill command/job

### 10.4 Versioning
Store a `qualityVersion` integer so the score formula can evolve over time.

### 10.5 Explainability
Store either:
- `qualityBreakdown` + `qualityFlags`, or
- enough intermediate metadata to reconstruct them deterministically

Preferred approach:
- persist both breakdown and flags for visibility/debugging.

## 11) Suggested heuristic rules (v1)
### Completeness heuristics
- name length >= 5
- description length >= 30
- ingredient count >= 3
- instruction step count >= 3
- servings present
- prep/cook/total time present

### Specificity heuristics
- at least 60% of ingredients have numeric quantity
- at least 60% of ingredients have recognized unit
- step text average length exceeds a minimal threshold
- presence of time references improves score
- presence of temperature references improves score
- excessive vague phrases reduce score

### Media heuristics
- image present = strong positive
- no image = lower but not catastrophic

## 12) Threshold recommendations
### Suggested thresholds
- `< 0.50` → needs improvement
- `0.50 - 0.69` → usable / acceptable
- `0.70 - 0.84` → strong recipe
- `>= 0.85` → high-quality recipe

## 13) Migration / rollout plan
### Phase 1
- add score fields to persistence model
- compute score on create/update/upload
- expose score in API

### Phase 2
- add `minQualityScore` filter in search/list endpoint
- apply default threshold for public search

### Phase 3
- run bulk backfill on existing recipes
- identify low-score recipes for editorial cleanup

## 14) Risks
- overweighting image presence and hiding useful text-only recipes
- overweighting strict structure and penalizing simple but effective recipes
- formula changes causing unstable scores unless versioned
- confusion if public filtering and internal visibility are not clearly separated

## 15) Recommendation
Proceed with a deterministic heuristic v1 owned by the backend, computed at create/update/image-upload time, stored with versioned breakdown metadata, and used to enforce a public search threshold (starting at `0.5`).
