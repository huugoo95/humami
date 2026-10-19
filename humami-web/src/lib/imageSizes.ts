// `sizes` hints for remote images, matching the layouts that render them, so browsers pick
// a variant close to the displayed width instead of the largest one.
export const IMAGE_SIZES = {
  // Meal grid: 1 column, 2 from md (768px), 3 from xl (1280px), inside the capped page container.
  mealCard: "(min-width: 1536px) 480px, (min-width: 1280px) 33vw, (min-width: 768px) 50vw, 100vw",
  // Meal detail article: max-w-5xl (1024px) with padding.
  mealDetail: "(min-width: 1088px) 960px, 100vw",
  // Blog article: max-w-4xl (896px) with padding.
  blogCover: "(min-width: 960px) 816px, 100vw",
} as const;

// Meal cards rendered above the fold on the largest layout (one row of three).
export const PRIORITY_MEAL_CARDS = 3;
