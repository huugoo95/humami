# Spec 029 — Show the guide access button

## Why

On production guide landings the "Ver la guía ahora" submit button renders transparent with text in the same colour as the form panel, so visitors cannot see how to open the guide. Tailwind only scans `src/app`, `src/components` and `src/pages`; classes used only in `src/features/**` (such as `bg-humami-gold` and `text-humami-accent-dark`) are never generated.

## Scope

Include `src/features/**` in the Tailwind content globs and guard that every frontend source directory holding components is scanned. No change to the guide access API, lead storage or copy.

## Acceptance criteria

- The guide access submit button has a visible gold background that contrasts with the form panel.
- Tailwind scans every `src` directory that contains components, including `src/features`.
- Lead capture and redirection to the reader behave as before.
