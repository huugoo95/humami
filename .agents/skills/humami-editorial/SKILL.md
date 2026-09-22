---
name: humami-editorial
description: Draft Spanish articles for the Humami blog using brand voice and current plain-text rendering. Excludes LinkedIn, meal payloads and publication.
---

# Draft a blog article

Read [brand](../../../docs/BRAND.md) and [editorial knowledge](../../../docs/CONTENT.md). Establish audience, purpose, source material and CTA from the request; distinguish shipped features from plans.

- Write useful Spanish culinary content in the requested length and voice. Record sources for factual claims and flag unsupported statements rather than inventing evidence.
- The current blog renders the content field as plain text with preserved line breaks, not Markdown or HTML. Prepare readable plain-text sections; do not promise rendered headings, links or tables.
- For an API-ready draft, consult only [BlogPostRequest](../../../humami-backend/src/main/java/com/hugo/humami/dto/request/BlogPostRequest.java): title, slug, excerpt, content, author, tags and SEO fields as appropriate. Keep it local; writing a remote draft is still a write operation.
- Save an actual requested draft under `content/articles/` with accompanying source/review notes; do not create empty scaffolding. Do not invent an author, publication date or public status.

Return draft and review notes. No Git Flow/deployment procedure is needed just to compose text; if saving project files, retain a valid work branch using only the branch setup phase of code delivery. Use content publication only on a publishing request. LinkedIn is outside this skill.
