#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${1:-https://humami.es}"

check() {
  local path="$1"
  local code
  code=$(curl -sS -o /tmp/humami-smoke.out -w '%{http_code}' "${BASE_URL}${path}")
  if [[ "$code" != "200" ]]; then
    echo "[FAIL] ${path} -> HTTP ${code}"
    head -c 300 /tmp/humami-smoke.out || true
    echo
    return 1
  fi
  echo "[OK] ${path} -> HTTP 200"
}

check "/"
check "/meals"
check "/api/meals?query=&page=1&limit=5"

echo "Smoke checks passed for ${BASE_URL}"
