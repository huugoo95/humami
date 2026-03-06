#!/usr/bin/env bash
set -euo pipefail

# Run once per clone/repository.

DEFAULT_REMOTE="${1:-origin}"

if ! git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
  echo "[git-flow] Not inside a git repository." >&2
  exit 1
fi

# Ensure develop exists locally/remotely.
if git show-ref --verify --quiet refs/heads/develop; then
  echo "[git-flow] Local branch 'develop' already exists."
else
  git checkout -b develop master
  echo "[git-flow] Created local 'develop' from 'master'."
fi

git push -u "$DEFAULT_REMOTE" develop

# Configure git-flow metadata (works with git-flow-avh if installed).
git config gitflow.branch.master master
git config gitflow.branch.develop develop
git config gitflow.prefix.feature feat/
git config gitflow.prefix.bugfix fix/
git config gitflow.prefix.release release/
git config gitflow.prefix.hotfix hotfix/
git config gitflow.prefix.support support/
git config gitflow.prefix.versiontag ""

echo "[git-flow] Configured branches and prefixes:"
echo "  master  -> master"
echo "  develop -> develop"
echo "  feat/*, fix/*, release/*, hotfix/*"
