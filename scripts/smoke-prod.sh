#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${1:-https://humami.es}"
WAIT_TIMEOUT_SECONDS="${WAIT_TIMEOUT_SECONDS:-480}"
WAIT_INTERVAL_SECONDS="${WAIT_INTERVAL_SECONDS:-3}"
SMOKE_TMP_DIR=$(mktemp -d)
trap 'rm -rf "$SMOKE_TMP_DIR"' EXIT

check() {
  local path="$1"
  local code
  local output_file="$SMOKE_TMP_DIR/check.out"
  : > "$output_file"
  code=$(curl --connect-timeout 10 --max-time 15 -sS -o "$output_file" -w '%{http_code}' "${BASE_URL}${path}" || true)
  if [[ "$code" != "200" ]]; then
    echo "[FAIL] ${path} -> HTTP ${code}"
    head -c 300 "$output_file" || true
    echo
    return 1
  fi
  echo "[OK] ${path} -> HTTP 200"
}

wait_for_200() {
  local path="$1"
  local deadline=$(( $(date +%s) + WAIT_TIMEOUT_SECONDS ))
  local output_file="$SMOKE_TMP_DIR/readiness.out"
  local code="000"
  : > "$output_file"

  while :; do
    local now
    now=$(date +%s)
    if (( now >= deadline )); then
      echo "[TIMEOUT] ${path} did not become ready within ${WAIT_TIMEOUT_SECONDS}s (last HTTP ${code})"
      head -c 300 "$output_file" || true
      echo
      return 1
    fi

    local remaining_seconds=$(( deadline - now ))
    local request_timeout_seconds=$(( remaining_seconds < 15 ? remaining_seconds : 15 ))
    local connect_timeout_seconds=$(( request_timeout_seconds < 10 ? request_timeout_seconds : 10 ))
    : > "$output_file"
    code=$(curl --connect-timeout "$connect_timeout_seconds" --max-time "$request_timeout_seconds" -sS -o "$output_file" -w '%{http_code}' "${BASE_URL}${path}" || true)

    if [[ "$code" == "200" ]]; then
      echo "[READY] ${path} -> HTTP 200"
      return 0
    fi

    now=$(date +%s)
    if (( now >= deadline )); then
      echo "[TIMEOUT] ${path} did not become ready within ${WAIT_TIMEOUT_SECONDS}s (last HTTP ${code})"
      head -c 300 "$output_file" || true
      echo
      return 1
    fi

    remaining_seconds=$(( deadline - now ))
    local sleep_seconds=$(( WAIT_INTERVAL_SECONDS < remaining_seconds ? WAIT_INTERVAL_SECONDS : remaining_seconds ))
    sleep "$sleep_seconds"
  done
}

# readiness gate (avoid false negatives right after deploy)
wait_for_200 "/api/meals?query=&page=1&limit=5"

check "/"
check "/meals"
check "/api/meals?query=&page=1&limit=5"

echo "Smoke checks passed for ${BASE_URL}"
