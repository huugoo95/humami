# Spec 033 — Free disk space before production image pulls (hotfix)

## Why

The release 0.5.3 deployment (run 39239363128, master 7c105ce) passed the spec 031 script checksums. It then failed while extracting the new frontend image: `no space left on device` under `/var/lib/containerd`. No container was recreated, so production stayed on 0.5.2.

Every deployment pulls new SHA-tagged images, and nothing ever removes the old ones. The same host also runs dev.humami.es. The dev deployments have been timing out (backend start 173 s, MongoDB round-trip 12 s), which is consistent with an exhausted host. The repository has no SSH access to clean the host by hand, so the deployment itself must reclaim space.

## Scope

In `scripts/deploy-images.sh`, before pulling:

- Print disk usage (`df -h /`, `docker system df`) for diagnosis.
- Remove images that no container uses (`docker image prune -af`), plus the build cache. Images of running or stopped containers, including the current production release and the dev environment, are kept, so the running release stays available for recovery.
- Check that at least `MIN_FREE_MB` (default 3072 MB) is free on the Docker data filesystem. Otherwise fail before pulling, with the diagnostics above.

No change to Compose services, volumes, images or smoke checks. Volumes are never pruned.

## Acceptance criteria

- The deployment prunes unused images and build cache before pulling, and logs disk usage before and after.
- Images used by existing containers and all volumes are preserved.
- With less free space than required after pruning, the deployment fails before pulling or recreating containers.
- Release 0.5.3's content deploys to humami.es through this hotfix (0.5.4).
