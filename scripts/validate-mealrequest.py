#!/usr/bin/env python3
"""Validate Humami MealRequest JSON payload.

Usage:
  python3 scripts/validate-mealrequest.py path/to/payload.json
"""

from __future__ import annotations

import json
import sys
from pathlib import Path

ALLOWED_DIFFICULTY = {"EASY", "INTERMEDIATE", "HARD"}
ALLOWED_MEAL_TYPE = {
    "BREAKFAST", "BRUNCH", "STARTER", "MAIN", "SIDE", "SAUCE",
    "DESSERT", "SNACK", "DRINK", "BREAD", "SOUP"
}
ALLOWED_UNITS = {
    "TEASPOON", "TABLESPOON", "MILLILITER", "LITER", "CUP", "GRAM",
    "KILOGRAM", "MILLIGRAM", "PIECE", "UNIT", "SLICE", "LEAF",
    "CLOVE", "PINCH", "HANDFUL", "TO_TASTE", "DASH", "DROP"
}


def err(msg: str) -> None:
    print(f"❌ {msg}")


def ok(msg: str) -> None:
    print(f"✅ {msg}")


def validate(payload: dict) -> list[str]:
    errors: list[str] = []

    if not isinstance(payload.get("name"), str) or not payload["name"].strip():
        errors.append("name is required and must be a non-empty string")

    if not isinstance(payload.get("description"), str) or not payload["description"].strip():
        errors.append("description is required and must be a non-empty string")

    recipes = payload.get("recipes")
    if not isinstance(recipes, list) or len(recipes) == 0:
        errors.append("recipes is required and must be a non-empty array")
    else:
        for i, recipe in enumerate(recipes):
            prefix = f"recipes[{i}]"
            if not isinstance(recipe, dict):
                errors.append(f"{prefix} must be an object")
                continue

            if not isinstance(recipe.get("name"), str) or not recipe["name"].strip():
                errors.append(f"{prefix}.name is required")

            steps = recipe.get("instructionSteps")
            legacy = recipe.get("instructions")
            has_steps = isinstance(steps, list) and len(steps) > 0
            has_legacy = isinstance(legacy, list) and len(legacy) > 0
            if not (has_steps or has_legacy):
                errors.append(f"{prefix} must include instructionSteps[] or instructions[]")

            ingredients = recipe.get("ingredients")
            if ingredients is not None:
                if not isinstance(ingredients, list):
                    errors.append(f"{prefix}.ingredients must be an array when present")
                else:
                    for j, ing in enumerate(ingredients):
                        ip = f"{prefix}.ingredients[{j}]"
                        if not isinstance(ing, dict):
                            errors.append(f"{ip} must be an object")
                            continue
                        if not isinstance(ing.get("name"), str) or not ing["name"].strip():
                            errors.append(f"{ip}.name is required")
                        qty_present = "quantity" in ing and ing.get("quantity") is not None
                        unit = ing.get("unit")
                        if qty_present and unit not in ALLOWED_UNITS:
                            errors.append(f"{ip}.unit must be one of {sorted(ALLOWED_UNITS)} when quantity is present")
                        if unit is not None and unit not in ALLOWED_UNITS:
                            errors.append(f"{ip}.unit is invalid: {unit}")

    difficulty = payload.get("difficulty")
    if difficulty not in ALLOWED_DIFFICULTY:
        errors.append(f"difficulty must be one of {sorted(ALLOWED_DIFFICULTY)}")

    meal_type = payload.get("type")
    if meal_type not in ALLOWED_MEAL_TYPE:
        errors.append(f"type must be one of {sorted(ALLOWED_MEAL_TYPE)}")

    servings = payload.get("servings")
    if not isinstance(servings, int) or servings <= 0:
        errors.append("servings must be an integer > 0")

    timings = payload.get("timings")
    if timings is not None:
        if not isinstance(timings, dict):
            errors.append("timings must be an object when present")
        else:
            for field in ("prepTimeInHours", "totalTimeInHours"):
                value = timings.get(field)
                if value is not None and not isinstance(value, (int, float)):
                    errors.append(f"timings.{field} must be numeric when present")

    return errors


def main() -> int:
    if len(sys.argv) != 2:
        err("Usage: python3 scripts/validate-mealrequest.py path/to/payload.json")
        return 2

    path = Path(sys.argv[1])
    if not path.exists():
        err(f"File not found: {path}")
        return 2

    try:
        payload = json.loads(path.read_text(encoding="utf-8"))
    except json.JSONDecodeError as e:
        err(f"Invalid JSON: {e}")
        return 2

    if not isinstance(payload, dict):
        err("Top-level JSON must be an object")
        return 2

    errors = validate(payload)
    if errors:
        for e in errors:
            err(e)
        return 1

    ok("MealRequest preflight passed")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
