import type { Guide } from "@/types/guide";

export const pizzaGuidePreview: Guide = {
  id: "preview-pizza-napolitana",
  slug: "pizza-napolitana-desde-cero",
  title: "Tu primera pizza napolitana en casa",
  excerpt:
    "Una guía práctica para preparar pizza napolitana en un horno doméstico, sin amasar y con una planificación sencilla de la mañana a la noche.",
  landingHeadline: "De la mañana a la noche, paso a paso.",
  landingDescription: "Una ruta para preparar tu primera pizza napolitana con horno normal, sin necesidad de amasar ni de comprar equipo especial.",
  duration: "8 horas · 45 min activos",
  level: "Principiante",
  pageCount: 13,
  contents: [
    "Qué necesitas: harina, ingredientes y equipo mínimo",
    "La masa: mezcla, reposos y pliegues",
    "Boleado, fermentación y cómo abrir la masa",
    "Horneado en horno doméstico",
    "Errores comunes y cómo corregirlos",
  ],
};

export function getDevelopmentGuides(): Guide[] {
  return process.env.NODE_ENV === "development" ? [pizzaGuidePreview] : [];
}
