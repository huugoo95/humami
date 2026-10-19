#!/usr/bin/env bash
# Runs ON the production host, piped over SSH by .github/workflows/publish-content.yml. It calls a
# Humami write endpoint with the write secret read from the host's .env, so the secret never leaves
# the host and never appears in logs.
# Usage: publish-content.sh <METHOD> </api/path> <base64-encoded JSON payload>
set -euo pipefail

method="$1"
path="$2"
payload="$(printf '%s' "$3" | base64 -d)"

# Re-validate on the host: no other methods, and no dot-only segments that could escape /api/.
[[ "$method" =~ ^(PATCH|POST|PUT)$ ]] || { echo "Method not allowed: ${method}"; exit 1; }
[[ "$path" =~ ^/api/(about|meals|blog|guides)(/[A-Za-z0-9_-][A-Za-z0-9._-]*)*$ ]] || { echo "Path not allowed: ${path}"; exit 1; }
env_file="${HUMAMI_ENV_FILE:-/home/ubuntu/apps/humami/.env}"

secret="$(tr -d '\r' < "$env_file" | sed -n -E "s/^[[:space:]]*(export[[:space:]]+)?HUMAMI_WRITE_SECRET=//p" | tail -n 1 \
  | sed -E "s/^\"(.*)\"$/\\1/; s/^'(.*)'$/\\1/")"
if [[ -z "$secret" ]]; then
  echo "HUMAMI_WRITE_SECRET not found in ${env_file}"
  exit 1
fi

response="$(mktemp)"
trap 'rm -f "$response"' EXIT
status="$(printf '%s' "$payload" | curl -sS --path-as-is --max-time 60 -o "$response" -w '%{http_code}' -X "$method" "https://humami.es${path}" \
  -H "Content-Type: application/json" -H @<(printf 'X-HUMAMI-SECRET: %s\n' "$secret") --data-binary @-)"

cat "$response"
echo
echo "[publish] ${method} ${path} -> HTTP ${status}"
[[ "$status" =~ ^2 ]]
