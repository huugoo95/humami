import { Meal } from "@/types/meal";
import apiClient from "@/config/api";
import Image from "next/image";
import Link from "next/link";
import { IMAGE_SIZES } from "@/lib/imageSizes";
import { notFound } from "next/navigation";
import type { Metadata } from "next";
import { mealMetadata } from "@/lib/brandMetadata";
import {
  buildRecipeJsonLd,
  formatDuration,
  formatIngredientAmount,
  recipeSteps,
  splitLinks,
} from "@/lib/recipeFormat";

const SITE_URL = "https://humami.es";

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
  if (!value) return null;
  const key = value.toUpperCase();
  return difficultyLabels[key] ?? value.toLowerCase();
}

function formatType(value?: string) {
  if (!value) return null;
  const key = value.toUpperCase();
  return typeLabels[key] ?? value.toLowerCase();
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

function LinkedText({ text }: { text: string }) {
  return (
    <>
      {splitLinks(text).map((part, index) =>
        part.href ? (
          <a
            key={index}
            href={part.href}
            target="_blank"
            rel="noopener noreferrer"
            className="break-words font-medium text-humami-accent underline underline-offset-2 hover:text-humami-accent-dark"
          >
            {part.text}
          </a>
        ) : (
          <span key={index}>{part.text}</span>
        ),
      )}
    </>
  );
}

export default async function MealPage({ params }: { params: { id: string } }) {
  const meal = await getMealById(params.id);
  if (!meal) return notFound();

  const recipes = meal.recipes ?? [];
  const typeLabel = formatType(meal.type);
  const facts = [
    { label: "Tiempo activo", value: formatDuration(meal.timings?.prepTimeInHours) },
    { label: "Tiempo total", value: formatDuration(meal.timings?.totalTimeInHours) },
    { label: "Raciones", value: meal.servings ? String(meal.servings) : null },
    { label: "Dificultad", value: formatDifficulty(meal.difficulty) },
    { label: "Tipo", value: typeLabel },
  ].filter((fact): fact is { label: string; value: string } => Boolean(fact.value));
  const jsonLd = buildRecipeJsonLd(meal, SITE_URL, typeLabel ?? undefined);

  return (
    <article className="mx-auto max-w-5xl sm:rounded-2xl sm:border sm:border-humami-text-base/10 sm:bg-white/70 sm:p-8 sm:shadow-sm">
      <script
        type="application/ld+json"
        dangerouslySetInnerHTML={{ __html: JSON.stringify(jsonLd).replace(/</g, "\\u003c") }}
      />

      <Link
        href="/meals"
        className="mb-4 inline-flex items-center gap-1 text-sm font-medium text-humami-accent hover:text-humami-accent-dark"
      >
        <span aria-hidden="true">←</span> Todas las recetas
      </Link>

      <header>
        <h1 className="mb-3 font-heading text-3xl leading-tight text-humami-text-heading sm:text-5xl">
          {meal.name}
        </h1>
        <p className="mb-6 text-base leading-relaxed text-humami-text-base sm:text-lg">{meal.description}</p>

        {facts.length > 0 && (
          <dl className="mb-6 grid grid-cols-2 gap-px overflow-hidden rounded-xl border border-humami-text-base/10 bg-humami-text-base/10 max-sm:[&>div:last-child:nth-child(odd)]:col-span-2 sm:grid-cols-5">
            {facts.map((fact) => (
              <div key={fact.label} className="bg-white px-4 py-3">
                <dt className="text-xs font-medium uppercase tracking-wide text-humami-text-base/60">{fact.label}</dt>
                <dd className="mt-0.5 font-semibold first-letter:uppercase text-humami-text-heading">{fact.value}</dd>
              </div>
            ))}
          </dl>
        )}
      </header>

      {meal.image && (
        <div className="mb-8">
          <Image
            src={meal.image}
            alt={meal.name}
            className="aspect-video h-auto w-full rounded-xl border border-humami-text-base/10 object-cover shadow-sm"
            width={1600}
            height={900}
            sizes={IMAGE_SIZES.mealDetail}
            priority
          />
        </div>
      )}

      {recipes.length > 1 && (
        <nav aria-label="Elaboraciones" className="mb-8 rounded-xl border border-humami-text-base/10 bg-white p-4">
          <p className="mb-2 text-xs font-medium uppercase tracking-wide text-humami-text-base/60">
            {recipes.length} elaboraciones
          </p>
          <ol className="flex flex-wrap gap-2">
            {recipes.map((recipe, idx) => (
              <li key={idx}>
                <a
                  href={`#elaboracion-${idx + 1}`}
                  className="inline-flex items-center gap-2 rounded-full border border-humami-accent/20 bg-white px-3 py-1.5 text-sm font-medium text-humami-accent-dark hover:border-humami-accent/50"
                >
                  <span className="text-humami-gold">{idx + 1}</span>
                  {recipe.name}
                </a>
              </li>
            ))}
          </ol>
        </nav>
      )}

      {recipes.map((recipe, idx) => {
        const steps = recipeSteps(recipe);
        const ingredients = recipe.ingredients ?? [];
        return (
          <section
            key={idx}
            id={`elaboracion-${idx + 1}`}
            className="scroll-mt-6 border-t border-humami-text-base/10 py-8 first-of-type:border-t-0 first-of-type:pt-0"
          >
            <h2 className="font-heading text-2xl text-humami-accent-dark sm:text-3xl">
              {recipes.length > 1 && <span className="mr-2 text-humami-gold">{idx + 1}.</span>}
              {recipe.name}
            </h2>
            {recipe.description && (
              <p className="mt-2 max-w-3xl leading-relaxed text-humami-text-base/80">{recipe.description}</p>
            )}

            <div className="mt-5 grid gap-6 lg:grid-cols-[minmax(0,2fr)_minmax(0,3fr)] lg:gap-10">
              {ingredients.length > 0 && (
                <div className="h-fit rounded-xl border border-humami-text-base/10 bg-white p-4 sm:p-5">
                  <h3 className="mb-2 text-sm font-semibold uppercase tracking-wide text-humami-accent-dark">
                    Ingredientes
                  </h3>
                  <ul className="divide-y divide-humami-text-base/10">
                    {ingredients.map((ingredient, iIndex) => {
                      const amount = formatIngredientAmount(ingredient.quantity, ingredient.unit);
                      return (
                        <li key={iIndex} className="flex items-baseline justify-between gap-4 py-2 text-sm sm:text-base">
                          <span className="leading-snug text-humami-text-base">
                            {ingredient.name}
                            {ingredient.isOptional && (
                              <span className="ml-1.5 text-xs text-humami-text-base/60">(opcional)</span>
                            )}
                          </span>
                          {amount && (
                            <span className="shrink-0 text-right font-semibold tabular-nums text-humami-text-heading">
                              {amount}
                            </span>
                          )}
                        </li>
                      );
                    })}
                  </ul>
                </div>
              )}

              {steps.length > 0 && (
                <div>
                  <h3 className="mb-3 text-sm font-semibold uppercase tracking-wide text-humami-accent-dark">
                    Paso a paso
                  </h3>
                  <ol className="space-y-4">
                    {steps.map((step, sIndex) => (
                      <li key={sIndex} className="flex gap-3">
                        <span
                          aria-hidden="true"
                          className="mt-0.5 flex h-7 w-7 shrink-0 items-center justify-center rounded-full bg-humami-accent text-sm font-semibold text-white"
                        >
                          {sIndex + 1}
                        </span>
                        <p className="leading-relaxed text-humami-text-base">{step}</p>
                      </li>
                    ))}
                  </ol>
                </div>
              )}
            </div>
          </section>
        );
      })}

      {meal.faqs && meal.faqs.length > 0 && (
        <section className="border-t border-humami-text-base/10 pt-8">
          <h2 className="mb-4 font-heading text-2xl text-humami-accent-dark sm:text-3xl">Preguntas frecuentes</h2>
          <div className="divide-y divide-humami-text-base/10 rounded-xl border border-humami-text-base/10 bg-white">
            {meal.faqs.map((faq, index) => (
              <details key={index} className="group px-4 sm:px-5">
                <summary className="flex cursor-pointer list-none items-center justify-between gap-4 py-4 font-semibold text-humami-accent-dark [&::-webkit-details-marker]:hidden">
                  {faq.question}
                  <span
                    aria-hidden="true"
                    className="shrink-0 text-xl leading-none text-humami-gold transition-transform group-open:rotate-45"
                  >
                    +
                  </span>
                </summary>
                <p className="pb-4 leading-relaxed text-humami-text-base">
                  <LinkedText text={faq.answer} />
                </p>
              </details>
            ))}
          </div>
        </section>
      )}
    </article>
  );
}
