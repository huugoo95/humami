import { Meal } from "@/types/meal";
import apiClient from "@/config/api";
import Image from "next/image";
import { notFound } from "next/navigation";
import type { Metadata } from "next";
import { mealMetadata } from "@/lib/brandMetadata";

const difficultyLabels: Record<string, string> = {
  EASY: "fácil",
  INTERMEDIATE: "media",
  HARD: "difícil",
};

const typeLabels: Record<string, string> = {
  BREAKFAST: "desayuno",
  BRUNCH: "brunch",
  STARTER: "entrante",
  MAIN: "principal",
  SIDE: "acompañamiento",
  SAUCE: "salsa",
  DESSERT: "postre",
  SNACK: "snack",
  DRINK: "bebida",
  BREAD: "pan",
  SOUP: "sopa",
};

function formatDifficulty(value?: string) {
  if (!value) return "-";
  const key = value.toUpperCase();
  return difficultyLabels[key] ?? value.toLowerCase();
}

function formatType(value?: string) {
  if (!value) return "-";
  const key = value.toUpperCase();
  return typeLabels[key] ?? value.toLowerCase();
}

function formatIngredientUnit(quantity: number, unit?: string) {
  if (!unit) return "";

  const normalized = unit.toLowerCase();
  const isPlural = quantity > 1;

  if (!isPlural) return normalized;

  const pluralMap: Record<string, string> = {
    unidad: "unidades",
    diente: "dientes",
    cucharada: "cucharadas",
    cucharadita: "cucharaditas",
    rama: "ramas",
    hoja: "hojas",
    rodaja: "rodajas",
    rebanada: "rebanadas",
    taza: "tazas",
    vaso: "vasos",
    lata: "latas",
    paquete: "paquetes",
  };

  return pluralMap[normalized] ?? normalized;
}

async function getMealById(id: string): Promise<Meal | null> {
  try {
    const response = await apiClient.get(`/meals/${id}`);
    return response.data;
  } catch (error) {
    console.error(`No se pudo obtener el Meal con id ${id}:`, error);
    return null;
  }
}

export async function generateMetadata({ params }: { params: { id: string } }): Promise<Metadata> {
  const meal = await getMealById(params.id);
  if (!meal) {
    return {
      title: "Receta no encontrada",
      description: "La receta solicitada no existe.",
    };
  }

  return mealMetadata(meal);
}

export default async function MealPage({ params }: { params: { id: string } }) {
  const meal = await getMealById(params.id);
  if (!meal) return notFound();

  return (
    <article className="mx-auto max-w-5xl rounded-2xl border border-humami-text-base/10 bg-white/70 p-5 shadow-sm sm:p-8">
      <h1 className="mb-3 font-heading text-4xl text-humami-text-heading sm:text-5xl">
        {meal.name}
      </h1>
      <p className="mb-5 text-base leading-relaxed text-humami-text-base sm:text-lg">{meal.description}</p>

      {/* METADATOS DEL MENÚ */}
      <div className="mb-6 flex flex-wrap gap-2 text-xs text-humami-accent-dark sm:text-sm">
        <span className="rounded-full border border-humami-accent/20 bg-humami-bg-light px-3 py-1 font-medium">
          Dificultad: {formatDifficulty(meal.difficulty)}
        </span>
        <span className="rounded-full border border-humami-accent/20 bg-humami-bg-light px-3 py-1 font-medium">
          Tipo: {formatType(meal.type)}
        </span>
        <span className="rounded-full border border-humami-accent/20 bg-humami-bg-light px-3 py-1 font-medium">
          Raciones: {meal.servings}
        </span>
      </div>

      {/* IMAGEN */}
      {meal.image && (
        <div className="mb-8">
          <Image
            src={meal.image}
            alt={meal.name}
            className="h-auto w-full rounded-xl border border-humami-text-base/10 shadow-sm"
            width={1200}
            height={800}
          />
        </div>
      )}

      {/* INGREDIENTES AGRUPADOS POR ELABORACIÓN */}
      {meal.ingredientsByRecipe && meal.ingredientsByRecipe.length > 0 && (
        <section className="mb-9 sm:mb-10">
          <h2 className="mb-4 font-heading text-3xl text-humami-accent-dark sm:text-4xl">
            Ingredientes
          </h2>

          <div className="space-y-3 sm:space-y-4">
            {meal.ingredientsByRecipe.map((group, gIndex) => (
              <div
                key={gIndex}
                className="rounded-xl border border-humami-text-base/10 border-l-4 border-l-humami-gold bg-white p-4 shadow-sm"
              >
                <h3 className="mb-2 text-lg font-semibold text-humami-text-heading sm:text-xl">
                  {group.recipeName}
                </h3>

                <ul className="grid grid-cols-1 gap-x-6 gap-y-1.5 text-humami-text-base lg:grid-cols-2">
                  {group.ingredients.map((ingredient, iIndex) => (
                    <li key={iIndex} className="grid grid-cols-[96px_1fr] gap-2 items-start">
                      <span className="whitespace-nowrap text-sm font-semibold tabular-nums text-humami-text-heading sm:text-base">
                        {ingredient.quantity} {formatIngredientUnit(ingredient.quantity, ingredient.unit)}
                      </span>
                      <span className="pl-1 leading-tight text-sm sm:text-base">de {ingredient.name}{ingredient.isOptional ? ' (opcional)' : ''}</span>
                    </li>
                  ))}
                </ul>
              </div>
            ))}
          </div>
        </section>
      )}

      {/* INSTRUCCIONES */}
      {meal.recipes && meal.recipes.length > 0 && (
        <section className="mb-9 sm:mb-10">
          <h2 className="mb-4 font-heading text-3xl text-humami-accent-dark sm:text-4xl">
            Instrucciones
          </h2>

          <div className="space-y-3 sm:space-y-4">
            {meal.recipes.map((recipe, idx) => (
              <div
                key={idx}
                className="rounded-xl border border-humami-text-base/10 border-l-4 border-l-humami-accent bg-white p-4 shadow-sm"
              >
                <h3 className="mb-2 text-lg font-semibold text-humami-accent-dark sm:text-xl">
                  {recipe.name}
                </h3>

                <ol className="list-decimal space-y-2 pl-5 text-humami-text-base">
                  {(recipe.instructionSteps && recipe.instructionSteps.length > 0
                    ? [...recipe.instructionSteps].sort((a, b) => (a.order ?? 0) - (b.order ?? 0)).map((step) => step.text)
                    : (recipe.instructions || [])
                  ).map((step, sIndex) => (
                    <li key={sIndex}>{step}</li>
                  ))}
                </ol>
              </div>
            ))}
          </div>
        </section>
      )}

      {/* PREGUNTAS FRECUENTES */}
      {meal.faqs && meal.faqs.length > 0 && (
        <section className="mt-8 sm:mt-10">
          <h2 className="mb-4 font-heading text-3xl text-humami-accent-dark">
            Preguntas frecuentes
          </h2>
          <div className="space-y-3 sm:space-y-4">
            {meal.faqs.map((faq, index) => (
              <div key={index} className="rounded-xl border border-humami-text-base/10 bg-white p-4 shadow-sm">
                <h3 className="mb-1 font-semibold text-humami-accent-dark">{faq.question}</h3>
                <p className="text-sm text-humami-text-base sm:text-base">{faq.answer}</p>
              </div>
            ))}
          </div>
        </section>
      )}
    </article>
  );
}
