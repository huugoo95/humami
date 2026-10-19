#!/usr/bin/env bash
# Copies objects from the legacy us-east-1 bucket to the EU buckets (spec 034), keeping keys
# so MongoDB references stay valid. Public images go to the media bucket with a one-year
# immutable Cache-Control; private/ objects go to the private bucket. Objects already present
# in the destination are skipped, so the script can be re-run for a delta copy after cutover.
# Usage: scripts/migrate-media-eu.sh [--dry-run]
set -euo pipefail

SOURCE_BUCKET="${SOURCE_BUCKET:-humami}"
SOURCE_REGION="${SOURCE_REGION:-us-east-1}"
MEDIA_BUCKET="${MEDIA_BUCKET:-humami-media-eu}"
PRIVATE_BUCKET="${PRIVATE_BUCKET:-humami-private-eu}"
TARGET_REGION="${TARGET_REGION:-eu-west-3}"
MEDIA_CACHE_CONTROL="public, max-age=31536000, immutable"
DRY_RUN="${1:-}"

exists() {
  aws s3api head-object --bucket "$1" --key "$2" --region "$TARGET_REGION" >/dev/null 2>&1
}

copied=0
skipped=0
while IFS= read -r key; do
  [[ -z "$key" || "$key" == */ ]] && continue
  if [[ "$key" == private/* ]]; then
    target="$PRIVATE_BUCKET"
  else
    target="$MEDIA_BUCKET"
  fi
  if exists "$target" "$key"; then
    skipped=$((skipped + 1))
    continue
  fi
  content_type="$(aws s3api head-object --bucket "$SOURCE_BUCKET" --key "$key" --region "$SOURCE_REGION" --query ContentType --output text)"
  echo "copy s3://$SOURCE_BUCKET/$key -> s3://$target/$key ($content_type)"
  if [[ "$DRY_RUN" != "--dry-run" ]]; then
    args=(--bucket "$target" --key "$key" --region "$TARGET_REGION"
      --copy-source "$SOURCE_BUCKET/$key" --metadata-directive REPLACE --content-type "$content_type")
    [[ "$target" == "$MEDIA_BUCKET" ]] && args+=(--cache-control "$MEDIA_CACHE_CONTROL")
    aws s3api copy-object "${args[@]}" >/dev/null
  fi
  copied=$((copied + 1))
done < <(aws s3api list-objects-v2 --bucket "$SOURCE_BUCKET" --region "$SOURCE_REGION" --query 'Contents[].Key' --output json \
  | python3 -c 'import json, sys; print("\n".join(json.load(sys.stdin) or []))')

echo "[migrate] copied=${copied} skipped=${skipped}${DRY_RUN:+ (dry run)}"
