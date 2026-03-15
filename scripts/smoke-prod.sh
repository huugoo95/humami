#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${1:-https://humami.es}"
WAIT_TIMEOUT_SECONDS="${WAIT_TIMEOUT_SECONDS:-90}"
WAIT_INTERVAL_SECONDS="${WAIT_INTERVAL_SECONDS:-3}"

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

wait_for_200() {
  local path="$1"
  local deadline=$(( $(date +%s) + WAIT_TIMEOUT_SECONDS ))

  while :; do
    local code
    code=$(curl -sS -o /tmp/humami-smoke-wait.out -w '%{http_code}' "${BASE_URL}${path}" || true)

    if [[ "$code" == "200" ]]; then
      echo "[READY] ${path} -> HTTP 200"
      return 0
    fi

    if (( $(date +%s) >= deadline )); then
      echo "[TIMEOUT] ${path} did not become ready within ${WAIT_TIMEOUT_SECONDS}s (last HTTP ${code})"
      head -c 300 /tmp/humami-smoke-wait.out || true
      echo
      return 1
    fi

    sleep "$WAIT_INTERVAL_SECONDS"
  done
}

# readiness gate (avoid false negatives right after deploy)
wait_for_200 "/api/meals?query=&page=1&limit=5"

check "/"
check "/meals"
check "/api/meals?query=&page=1&limit=5"

echo "Smoke checks passed for ${BASE_URL}"
