# About operations

Read [AboutRequest](../../../../humami-backend/src/main/java/com/hugo/humami/dto/request/AboutRequest.java) and [service](../../../../humami-backend/src/main/java/com/hugo/humami/service/impl/AboutServiceImpl.java) for fields and persistence behavior.

- Update: `PATCH /api/about`; title is text, story is a list of text paragraphs, photoUrl is text. Non-null fields are updated; sending an empty list replaces the story.
- Image: `PUT /api/about/image`, multipart field `image`.
- Verify: `GET /api/about` and `/our-story` after the authorized update.
- GET seeds the default record when no record exists. Do not use it as a supposedly non-mutating connectivity probe on an unknown production environment.
- The singleton is live content, not a versioned draft. Retain the prior known content for review/recovery, but do not automatically overwrite or restore without applicable authorization.
