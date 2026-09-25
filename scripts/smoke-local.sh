#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${1:-http://localhost:8080}"
MEAL_ID="${2:-does-not-exist}"
SECRET="${HUMAMI_WRITE_SECRET:-}"

if [[ -z "$SECRET" ]]; then
  echo "ERROR: HUMAMI_WRITE_SECRET is required"
  exit 1
fi

echo "[1/4] GET all meals (should be 200)"
curl -sS -o /tmp/humami_get_all.json -w "HTTP %{http_code}\n" "$BASE_URL/api/meals"

echo "[2/4] GET one meal (expect 200/404 depending on id)"
curl -sS -o /tmp/humami_get_one.json -w "HTTP %{http_code}\n" "$BASE_URL/api/meals/$MEAL_ID"

echo "[3/4] PATCH without secret (should be 401/403)"
curl -sS -o /tmp/humami_patch_no_secret.json -w "HTTP %{http_code}\n" \
  -X PATCH "$BASE_URL/api/meals/$MEAL_ID" \
  -H 'Content-Type: application/json' \
  -d '{}'

echo "[4/4] PATCH with secret (should pass auth: 200/404/500 depending on id/body)"
curl -sS -o /tmp/humami_patch_with_secret.json -w "HTTP %{http_code}\n" \
  -X PATCH "$BASE_URL/api/meals/$MEAL_ID" \
  -H 'Content-Type: application/json' \
  -H "X-HUMAMI-SECRET: $SECRET" \
  -d '{}'

echo "Done. Inspect /tmp/humami_*.json if needed."
