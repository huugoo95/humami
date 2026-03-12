import { Meal } from "@/types/meal";
import apiClient from "@/config/api";
import Image from "next/image";
import { notFound } from "next/navigation";

async function getMealById(id: string): Promise<Meal | null> {
  try {
    const response = await apiClient.get(`/meals/${id}`);
    return response.data;
  } catch (error) {
    console.error(`No se pudo obtener el Meal con id ${id}:`, error);
    return null;
  }
}

export default async function MealPage({ params }: { params: { id: string } }) {
  const meal = await getMealById(params.id);
  if (!meal) return notFound();

  return (
    <div className="max-w-6xl mx-auto px-3 py-4 sm:px-4 sm:py-6 md:px-6 bg-gray-50 shadow-sm md:shadow-lg rounded-none md:rounded-lg text-gray-900">
      <h1 className="text-3xl sm:text-4xl font-extrabold text-burgundy-800 mb-3">
        {meal.name}
      </h1>
      <p className="text-gray-700 text-base sm:text-lg mb-4">{meal.description}</p>

      {/* METADATOS DEL MENÚ */}
      <div className="flex flex-wrap gap-2 text-xs sm:text-sm text-gray-600 mb-5">
        <span className="bg-burgundy-100 text-burgundy-800 px-3 py-1 rounded-full font-medium">
          Dificultad: {meal.difficulty?.toLowerCase()}
        </span>
        <span className="bg-burgundy-100 text-burgundy-800 px-3 py-1 rounded-full font-medium">
          Tipo: {meal.type?.toLowerCase()}
        </span>
        <span className="bg-burgundy-100 text-burgundy-800 px-3 py-1 rounded-full font-medium">
          Raciones: {meal.servings}
        </span>
      </div>

      {/* IMAGEN */}
      {meal.image && (
        <div className="mb-5 sm:mb-6">
          <Image
            src={meal.image}
            alt={meal.name}
            className="w-full h-auto rounded-lg shadow"
            width={1200}
            height={800}
          />
        </div>
      )}

      {/* INGREDIENTES AGRUPADOS POR ELABORACIÓN */}
      {meal.ingredientsByRecipe && meal.ingredientsByRecipe.length > 0 && (
        <div className="mb-8 sm:mb-10">
          <h2 className="text-2xl sm:text-3xl font-semibold text-burgundy-800 mb-3 sm:mb-4">
            Ingredientes
          </h2>

          <div className="space-y-3 sm:space-y-4">
            {meal.ingredientsByRecipe.map((group, gIndex) => (
              <div
                key={gIndex}
                className="p-3 sm:p-4 bg-white border-l-4 border-burgundy-500 rounded shadow-sm"
              >
                <h3 className="text-lg sm:text-xl font-medium text-gray-900 mb-2">
                  {group.recipeName}
                </h3>

                <ul className="space-y-1.5 text-gray-700">
                  {group.ingredients.map((ingredient, iIndex) => (
                    <li key={iIndex} className="grid grid-cols-[96px_1fr] gap-2 items-start">
                      <span className="font-semibold text-gray-800 whitespace-nowrap">
                        {ingredient.quantity} {ingredient.unit?.toLowerCase()}
                      </span>
                      <span>{ingredient.name}</span>
                    </li>
                  ))}
                </ul>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* ELABORACIONES */}
      {meal.recipes.map((recipe, idx) => (
        <div
          key={idx}
          className="mb-6 sm:mb-8 p-3 sm:p-5 bg-white rounded-lg shadow-sm border-l-4 border-burgundy-700"
        >
          <h2 className="text-xl sm:text-2xl font-semibold text-burgundy-700 mb-2">
            {recipe.name}
          </h2>
          <p className="text-gray-600 mb-3 sm:mb-4 text-sm sm:text-base">{recipe.description}</p>

          <div className="p-3 sm:p-4 bg-gray-50 border-l-4 border-burgundy-700 rounded">
            <h3 className="text-lg sm:text-xl font-medium text-gray-900 mb-2">Instrucciones</h3>
            <ol className="list-decimal list-inside space-y-2 text-gray-700">
              {recipe.instructions.map((step, sIndex) => (
                <li key={sIndex} className="bg-burgundy-50 p-2 rounded-md">
                  {step}
                </li>
              ))}
            </ol>
          </div>
        </div>
      ))}

      {/* PREGUNTAS FRECUENTES */}
      {meal.faqs && meal.faqs.length > 0 && (
        <div className="mt-8 sm:mt-10">
          <h2 className="text-xl sm:text-2xl font-semibold text-burgundy-800 mb-3 sm:mb-4">
            Preguntas frecuentes
          </h2>
          <div className="space-y-3 sm:space-y-4">
            {meal.faqs.map((faq, index) => (
              <div key={index} className="bg-white p-3 sm:p-4 rounded shadow-sm">
                <h3 className="font-semibold text-burgundy-700 mb-1">{faq.question}</h3>
                <p className="text-gray-700 text-sm sm:text-base">{faq.answer}</p>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
