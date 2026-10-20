// Presentation helpers for the meal detail page: ingredient amounts, times and
// the schema.org Recipe data search engines read.

type IngredientLike = { name: string; quantity?: number | null; unit?: string | null };

const PLURAL_UNITS: Record<string, string> = {
  cucharadita: "cucharaditas",
  cucharada: "cucharadas",
  taza: "tazas",
  pieza: "piezas",
  unidad: "unidades",
  rebanada: "rebanadas",
  hoja: "hojas",
  diente: "dientes",
  pizca: "pizcas",
  "puñado": "puñados",
  chorro: "chorros",
  rama: "ramas",
  rodaja: "rodajas",
  vaso: "vasos",
  lata: "latas",
  paquete: "paquetes",
};

const TO_TASTE = "al gusto";

const FRACTIONS: Array<[number, string]> = [
  [0.25, "¼"],
  [0.5, "½"],
  [0.75, "¾"],
];

/** 0.5 → "½", 1.5 → "1½", 2.25 → "2¼", 0.3 → "0,3", 1000 → "1000". */
export function formatQuantity(quantity: number): string {
  const whole = Math.floor(quantity);
  const rest = quantity - whole;
  const fraction = FRACTIONS.find(([value]) => Math.abs(rest - value) < 0.001);
  if (fraction) return `${whole > 0 ? whole : ""}${fraction[1]}`;
  return Number(quantity.toFixed(2)).toString().replace(".", ",");
}

/**
 * Amount shown next to an ingredient, or null when there is nothing to show.
 * Counted units ("unidad") show only the number, and "al gusto" stands alone.
 */
export function formatIngredientAmount(quantity?: number | null, unit?: string | null): string | null {
  const normalized = unit?.trim().toLowerCase() || "";
  if (normalized === TO_TASTE) return TO_TASTE;
  if (quantity === null || quantity === undefined) return normalized || null;

  const amount = formatQuantity(quantity);
  if (!normalized || normalized === "unidad") return amount;
  const label = quantity > 1 ? PLURAL_UNITS[normalized] ?? normalized : normalized;
  return `${amount} ${label}`;
}

/** 0.75 → "45 min", 2 → "2 h", 3.5 → "3 h 30 min". */
export function formatDuration(hours?: number | null): string | null {
  if (!hours || hours <= 0) return null;
  const totalMinutes = Math.round(hours * 60);
  const h = Math.floor(totalMinutes / 60);
  const m = totalMinutes % 60;
  if (h === 0) return `${m} min`;
  return m === 0 ? `${h} h` : `${h} h ${m} min`;
}

/** 3.5 → "PT3H30M" (ISO 8601, as schema.org expects). */
export function toIsoDuration(hours?: number | null): string | undefined {
  if (!hours || hours <= 0) return undefined;
  const totalMinutes = Math.round(hours * 60);
  const h = Math.floor(totalMinutes / 60);
  const m = totalMinutes % 60;
  return `PT${h ? `${h}H` : ""}${m ? `${m}M` : ""}`;
}

export type TextPart = { text: string; href?: string };

const URL_PATTERN = /((?:https?:\/\/)?(?:www\.)?(?:youtube\.com\/watch\?v=[\w-]+|youtu\.be\/[\w-]+|[a-z0-9-]+\.[a-z]{2,}\/[^\s),;]*[^\s),;.]))/gi;

/** Splits text so bare links (e.g. "youtube.com/watch?v=…") can be rendered as anchors. */
export function splitLinks(text: string): TextPart[] {
  const parts: TextPart[] = [];
  let last = 0;
  for (const match of text.matchAll(URL_PATTERN)) {
    const start = match.index ?? 0;
    if (start > last) parts.push({ text: text.slice(last, start) });
    const url = match[0];
    parts.push({ text: url, href: url.startsWith("http") ? url : `https://${url}` });
    last = start + url.length;
  }
  if (last < text.length) parts.push({ text: text.slice(last) });
  return parts;
}

type RecipeLike = {
  name: string;
  ingredients?: IngredientLike[] | null;
  instructionSteps?: Array<{ order?: number; text: string }> | null;
  instructions?: string[] | null;
};

type MealLike = {
  id: string;
  name: string;
  description?: string;
  image?: string;
  type?: string;
  servings?: number;
  timings?: { prepTimeInHours?: number | null; totalTimeInHours?: number | null } | null;
  recipes?: RecipeLike[] | null;
  faqs?: Array<{ question: string; answer: string }> | null;
};

export function recipeSteps(recipe: RecipeLike): string[] {
  if (recipe.instructionSteps && recipe.instructionSteps.length > 0) {
    return [...recipe.instructionSteps].sort((a, b) => (a.order ?? 0) - (b.order ?? 0)).map((s) => s.text);
  }
  return recipe.instructions ?? [];
}

export function ingredientLine(ingredient: IngredientLike): string {
  const amount = formatIngredientAmount(ingredient.quantity, ingredient.unit);
  if (!amount) return ingredient.name;
  return amount === TO_TASTE ? `${ingredient.name}, ${TO_TASTE}` : `${amount} ${ingredient.name}`;
}

/** schema.org Recipe (plus FAQPage) for rich results. */
export function buildRecipeJsonLd(meal: MealLike, siteUrl: string, typeLabel?: string) {
  const recipes = meal.recipes ?? [];
  const multiple = recipes.length > 1;
  const recipe: Record<string, unknown> = {
    "@context": "https://schema.org",
    "@type": "Recipe",
    name: meal.name,
    description: meal.description,
    image: meal.image ? [meal.image] : undefined,
    url: `${siteUrl}/meals/${meal.id}`,
    author: { "@type": "Organization", name: "Humami" },
    recipeYield: meal.servings ? `${meal.servings} raciones` : undefined,
    recipeCategory: typeLabel,
    prepTime: toIsoDuration(meal.timings?.prepTimeInHours),
    totalTime: toIsoDuration(meal.timings?.totalTimeInHours),
    recipeIngredient: recipes.flatMap((r) => (r.ingredients ?? []).map(ingredientLine)),
    recipeInstructions: multiple
      ? recipes.map((r) => ({
          "@type": "HowToSection",
          name: r.name,
          itemListElement: recipeSteps(r).map((text) => ({ "@type": "HowToStep", text })),
        }))
      : recipes.flatMap((r) => recipeSteps(r).map((text) => ({ "@type": "HowToStep", text }))),
  };

  const graph: Record<string, unknown>[] = [recipe];
  if (meal.faqs && meal.faqs.length > 0) {
    graph.push({
      "@context": "https://schema.org",
      "@type": "FAQPage",
      mainEntity: meal.faqs.map((faq) => ({
        "@type": "Question",
        name: faq.question,
        acceptedAnswer: { "@type": "Answer", text: faq.answer },
      })),
    });
  }
  return graph;
}
