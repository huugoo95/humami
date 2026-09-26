# Downloadable content

Final, publishable downloads (guides, mini-books, checklists). One folder per piece; working drafts, raw photos and outlines stay out of this folder.

## Layout

```
content/downloads/<slug>/
  README.md                 # metadata and version history
  humami-<slug>.pdf         # current published version (stable filename)
```

- `<slug>`: short Spanish kebab-case name of the piece, e.g. `pizza-napolitana-en-casa`.
- The PDF keeps a stable filename so links and emails don't break; versions are tracked in the folder README and in git history, not in the filename.
- Every PDF uses brand logo v1 and the palette in [docs/BRAND.md](../../docs/BRAND.md), and credits the author as Hugo Pérez López.

## Distribution

These files are not served by the website. `humami-web/public/` is public and would bypass any email capture, so how each download is delivered (landing page, email, direct link) is decided when the lead-magnet landing is built and recorded in the piece's README.

## Adding a new download

1. Create `content/downloads/<slug>/` with the final PDF and a README from the template below.
2. Link it from the index in this file.
3. Deliver through a `docs/<short-name>` branch and PR to `develop`.

### README template

```
# <Title>
- Status: draft | ready | published
- Version: <n> (<YYYY-MM-DD>)
- Author: Hugo Pérez López
- Format: PDF, <pages> pages, A4
- Content pillar: see docs/CONTENT.md
- Distribution: <not published yet | landing URL | email>

## Versions
- v<n> (<date>): <what changed>
```

## Index

- [Tu primera pizza napolitana en casa](pizza-napolitana-en-casa/README.md) — lead magnet, ready.
