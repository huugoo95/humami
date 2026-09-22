# Blog operations

Read [BlogPostRequest](../../../../humami-backend/src/main/java/com/hugo/humami/dto/request/BlogPostRequest.java) and [service](../../../../humami-backend/src/main/java/com/hugo/humami/service/impl/BlogPostServiceImpl.java) for current fields and update semantics.

- Create: `POST /api/blog`; slug is required and duplicate slugs return conflict. Store returned ID and slug.
- Update: `PATCH /api/blog/{id}`; inspect the mapper before deciding which fields to omit.
- Cover: `PUT /api/blog/{id}/cover`, multipart field `image`.
- Public reads: `GET /api/blog` and `GET /api/blog/{slug}` expose only status `published`. There is no draft-read endpoint in the inspected controller.
- Set public status and a real `publishedAt` only when publication is requested. A local or remote draft does not prove a public article exists.
- Public body is plain text. Verify `/blog/{slug}` and the cover when publishing. A slug conflict or timeout calls for reconciliation, not overwrite or another blind create.
