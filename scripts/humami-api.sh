#!/usr/bin/env bash
# Calls a Humami production write endpoint with the write secret read from the macOS Keychain
# (service "humami-write-secret", account "humami"). The secret is passed to curl through a file
# descriptor, so it never appears in argv, output, logs or files.
#
# Usage:
#   scripts/humami-api.sh PATCH /api/about --json payload.json
#   scripts/humami-api.sh POST /api/meals --json - < meal.json
#   scripts/humami-api.sh PUT /api/meals/<id>/image --file image=photo.jpg
#
# Prints the response body and "HTTP <status>"; exits non-zero unless the status is 2xx.
set -euo pipefail

BASE_URL="${HUMAMI_API_BASE_URL:-https://humami.es}"
KEYCHAIN_SERVICE="humami-write-secret"
KEYCHAIN_ACCOUNT="humami"

usage() {
  sed -n '6,9p' "$0" | sed 's/^# *//'
  exit 2
}

[[ $# -ge 3 ]] || usage
method="$1"
path="$2"
mode="$3"
source="${4:-}"

[[ "$method" =~ ^(POST|PUT|PATCH)$ ]] || { echo "Method not allowed: ${method}" >&2; exit 2; }
# Allowlisted content APIs only; segments may not be dot-only, so the path cannot leave /api/.
[[ "$path" =~ ^/api/(about|meals|blog|guides)(/[A-Za-z0-9_-][A-Za-z0-9._-]*)*$ ]] || { echo "Path not allowed: ${path}" >&2; exit 2; }

case "$mode" in
  --json)
    [[ -n "$source" ]] || usage
    body_args=(-H "Content-Type: application/json" --data-binary "@${source}")
    if [[ "$source" != "-" ]]; then
      python3 -c 'import json, sys; json.load(open(sys.argv[1]))' "$source" || { echo "Invalid JSON: ${source}" >&2; exit 2; }
    fi
    ;;
  --file)
    [[ "$source" == *=* && -f "${source#*=}" ]] || { echo "Expected --file field=path to an existing file" >&2; exit 2; }
    body_args=(-F "${source%%=*}=@${source#*=}")
    ;;
  *) usage ;;
esac

secret="$(security find-generic-password -a "$KEYCHAIN_ACCOUNT" -s "$KEYCHAIN_SERVICE" -w 2>/dev/null)" || {
  echo "Write secret not found in the Keychain (service ${KEYCHAIN_SERVICE}, account ${KEYCHAIN_ACCOUNT})." >&2
  exit 1
}
[[ -n "$secret" ]] || { echo "The Keychain write secret is empty." >&2; exit 1; }

response="$(mktemp)"
trap 'rm -f "$response"' EXIT
status="$(curl -sS --path-as-is --max-time 120 -o "$response" -w '%{http_code}' -X "$method" "${BASE_URL}${path}" \
  -H @<(printf 'X-HUMAMI-SECRET: %s\n' "$secret") "${body_args[@]}")"
unset secret

cat "$response"
echo
echo "HTTP ${status}"
[[ "$status" =~ ^2 ]]
