# Spec019 progress and verification

## Completed preparation
- User approved deletion of frontend/backend5904567 images and latest aliases. Only those were removed. Disk63%/7GBfree ->55%/8.3GBfree. Active and prior images and all volumes retained.
- Production hostname15.188.51.29,ARM64,1.8GiB RAM,2CPUs; DNS dev.humami.es already points there. Production /,/meals,/api/meals returned200 before operations.
- Definition validated with OpenSpec strict; Compose config and diff checks pass. Independent review corrected potential service alias collisions and missing blog-cover upload block; final candidateb7d3aeb has no pending findings.
- User's brand/logo020 changes incorporated. PR62 merged as26070660011ae18473aa347588bd407fb00be255 after frontend/backend CI passed.
- Runtime directory /home/ubuntu/apps/humami-dev prepared. Independent credentials stored only on server in mode600 .env; no production credentials copied. Baseline image IDs and nginx/Compose backups stored in initial-backup.
- Additive HTTP challenge vhost validated with nginx-t and gracefully reloaded; no production application restarts. Separate dev certificate issued, expires2026-12-23. Existing renewal cron handles both lineages.

## Image build correction
Initial ARM64 run35987499641 failed because backend module context omitted root .mvn wrapper. Correct workflow copies root .mvn and mvnw into checkout module context, matching existing release-build-push.sh behavior. ARM64 image builds now also run on relevant PRs, but publication remains develop-only. No dev containers activated yet; final provenance/health/resources evidence follows after successful publication.
