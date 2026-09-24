# Design

- Asset: `public/brand/humami-wordmark-reverse.svg` (viewBox 754×204). Text is converted to paths, so rendering does not depend on Playfair Display being loaded.
- Rendering: `next/image` with `unoptimized` so the SVG is served as a static file (the default loader rejects SVG unless `dangerouslyAllowSVG` is enabled, which is not needed here). `priority` because it is above the fold on every page.
- Size: `h-8` (32 px) on mobile, `md:h-9` (36 px) from md up, `w-auto` to keep the aspect ratio. This keeps the wordmark above the 80 px minimum width in BRAND.md and the header height close to the current one.
- Accessibility: `alt="Humami"` gives the home link its accessible name.
- Colours come from the existing Tailwind palette: ivory `#F7F4EE`, soft-gold `#D4AF7F` on `humami-accent-dark` `#5F1E30`.
