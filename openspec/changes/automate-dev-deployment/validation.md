# Validation

## Local candidate

- `bash -n scripts/deploy-dev.sh` passed.
- Ruby `YAML.load_file` parsed `.github/workflows/dev-images.yml`.
- Shell-isolated tests supplied a fake registry token and mocked `docker`/`curl`: one verified a successful deployment record with digest pins and a full 40-character SHA; another forced a health timeout and verified that the previous Compose definition and image pins were restored, and the per-execution temporary backup was removed. A separate case rejected an invalid SHA before any registry or Compose operation. No server, registry or Docker daemon was used.
- `git diff --check` passed.
- OpenSpec CLI is not installed in this environment (`openspec: command not found`).

## Pending operational verification

The GitHub Actions secrets and a dedicated read-only package token must be configured after review and before the first merged run. End-to-end activation will be verified by the workflow against a real `develop` revision.
