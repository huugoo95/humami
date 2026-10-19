#!/usr/bin/env bash
# Runs ON the production host, piped over SSH by .github/workflows/publish-content.yml. It calls a
# Humami write endpoint with the write secret read from the host's .env, so the secret never leaves
# the host and never appears in logs.
# Usage: publish-content.sh <METHOD> </api/path> <base64-encoded JSON payload>
set -euo pipefail

method="$1"
path="$2"
payload="$(printf '%s' "$3" | base64 -d)"
env_file="${HUMAMI_ENV_FILE:-/home/ubuntu/apps/humami/.env}"

secret="$(sed -n -E "s/^(export[[:space:]]+)?HUMAMI_WRITE_SECRET=//p" "$env_file" | tail -n 1 | sed -E "s/^[\"']//; s/[\"']$//")"
if [[ -z "$secret" ]]; then
  echo "HUMAMI_WRITE_SECRET not found in ${env_file}"
  exit 1
fi

response="$(mktemp)"
trap 'rm -f "$response"' EXIT
status="$(printf '%s' "$payload" | curl -sS -o "$response" -w '%{http_code}' -X "$method" "https://humami.es${path}" \
  -H "Content-Type: application/json" -H @<(printf 'X-HUMAMI-SECRET: %s\n' "$secret") --data-binary @-)"

cat "$response"
echo
echo "[publish] ${method} ${path} -> HTTP ${status}"
[[ "$status" =~ ^2 ]]
