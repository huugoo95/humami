# Validation — Atlas-backed development database

Validated on 2026-09-24 after integrating the configuration through PR #65
(`b33709f`). No credentials or connection strings are recorded here.

## Migration evidence

- A dedicated Atlas user, `humami_dev_app`, has the `readWrite` role on
  `humami_dev` only.
- A final local MongoDB archive was made immediately after stopping the dev
  backend, then restored into Atlas. The server stores its checksum and the
  redacted result under its protected dev backup directory.
- Atlas inventory after restore: `meals` 51 documents and `about_pages` 1
  document; both retain their `_id_` indexes.
- The development backend reports the Atlas cluster host and `humami_dev`
  database at runtime. The previous local MongoDB container is stopped and its
  volume remains available for rollback.

## Runtime checks

| Target | Result |
| --- | --- |
| `https://dev.humami.es/` | 200 |
| `https://dev.humami.es/meals` | 200 |
| `GET /api/meals` in dev | 200; `totalItems: 51` |
| `GET /api/about` in dev | 200 |
| Production home, meals and meals API | 200; application containers unchanged |

## Rollback posture

The pre-cutover Compose configuration, MongoDB volume and final local archive
remain on the server. No volumes, images or production resources were deleted.
