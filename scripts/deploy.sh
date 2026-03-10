#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
APP_DIR="${APP_DIR:-$ROOT_DIR}"
DEPLOY_ENV_FILE="${DEPLOY_ENV_FILE:-/etc/humami/.env}"
DEPLOY_BRANCH="${DEPLOY_BRANCH:-master}"
HEALTHCHECK_URL="${HEALTHCHECK_URL:-https://humami.es/api/meals}"
HEALTHCHECK_TIMEOUT_SECONDS="${HEALTHCHECK_TIMEOUT_SECONDS:-120}"
DRY_RUN="${DRY_RUN:-0}"

log() { printf '[deploy] %s\n' "$*"; }
fail() { printf '[deploy][ERROR] %s\n' "$*" >&2; exit 1; }

run_cmd() {
  if [[ "$DRY_RUN" == "1" ]]; then
    printf '[deploy][DRY_RUN] %s\n' "$*"
  else
    eval "$@"
  fi
}

require_file() {
  local file="$1"
  [[ -f "$file" ]] || fail "Missing required file: $file"
}

require_bin() {
  local bin="$1"
  command -v "$bin" >/dev/null 2>&1 || fail "Missing required binary: $bin"
}

load_env_file() {
  require_file "$DEPLOY_ENV_FILE"
  set -a
  # shellcheck disable=SC1090
  source "$DEPLOY_ENV_FILE"
  set +a
}

healthcheck() {
  local end_ts status
  end_ts=$(( $(date +%s) + HEALTHCHECK_TIMEOUT_SECONDS ))

  log "Running healthcheck: $HEALTHCHECK_URL"
  while :; do
    status=$(curl -sS -o /tmp/humami-healthcheck.out -w '%{http_code}' "$HEALTHCHECK_URL" || true)
    if [[ "$status" == "200" ]]; then
      log "Healthcheck OK (HTTP 200)"
      return 0
    fi
    if (( $(date +%s) >= end_ts )); then
      log "Healthcheck failed, last status=$status"
      [[ -f /tmp/humami-healthcheck.out ]] && head -c 300 /tmp/humami-healthcheck.out || true
      return 1
    fi
    sleep 3
  done
}

main() {
  require_bin git
  require_bin curl

  if [[ "$DRY_RUN" != "1" ]]; then
    require_bin docker
  fi

  load_env_file

  cd "$APP_DIR"
  [[ -d .git ]] || fail "APP_DIR is not a git repo: $APP_DIR"

  mkdir -p .deploy-state

  local current_sha target_sha ts release_file
  current_sha="$(git rev-parse HEAD)"

  log "Fetching origin"
  run_cmd "git fetch origin"
  run_cmd "git checkout $DEPLOY_BRANCH"
  run_cmd "git pull --ff-only origin $DEPLOY_BRANCH"

  target_sha="$(git rev-parse HEAD)"
  ts="$(date +%Y%m%d-%H%M%S)"
  release_file=".deploy-state/release-$ts.txt"

  {
    echo "timestamp=$ts"
    echo "branch=$DEPLOY_BRANCH"
    echo "previous_sha=$current_sha"
    echo "target_sha=$target_sha"
  } > "$release_file"

  echo "$current_sha" > .deploy-state/previous_sha
  echo "$target_sha" > .deploy-state/target_sha

  log "Deploying commit $target_sha"
  run_cmd "docker compose --profile prod --env-file '$DEPLOY_ENV_FILE' up -d --build"

  if [[ "$DRY_RUN" == "1" ]]; then
    log "DRY_RUN enabled, skipping healthcheck"
  else
    healthcheck || fail "Deploy finished but healthcheck failed. Run: scripts/rollback.sh"
  fi

  echo "$target_sha" > .deploy-state/last_successful_sha
  log "Deploy completed successfully"
}

main "$@"
