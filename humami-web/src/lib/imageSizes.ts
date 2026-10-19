// `sizes` hints for remote images, matching the rendered width at each Tailwind breakpoint
// (main `container p-6` plus each page's own padding and max width), so browsers pick a
// variant close to the displayed width instead of the largest one.
export const IMAGE_SIZES = {
  // Meal grid (outer px-3/4/6, inner p-3/6, card p-3/4): max-w-md on mobile, 1 column from sm, 2 from md, 3 from xl, capped at max-w-7xl.
  mealCard:
    "(min-width: 1536px) 368px, (min-width: 1280px) 336px, (min-width: 1024px) 400px, (min-width: 768px) 272px, (min-width: 640px) 512px, calc(100vw - 120px)",
  // Meal detail article: max-w-5xl with p-5 / sm:p-8.
  mealDetail:
    "(min-width: 1280px) 960px, (min-width: 1024px) 912px, (min-width: 768px) 656px, (min-width: 640px) 528px, calc(100vw - 88px)",
  // Blog article: max-w-4xl with p-6 / md:p-10.
  blogCover: "(min-width: 1024px) 816px, (min-width: 768px) 640px, (min-width: 640px) 544px, calc(100vw - 96px)",
} as const;

// Meal cards rendered above the fold on the largest layout (one row of three).
export const PRIORITY_MEAL_CARDS = 3;
