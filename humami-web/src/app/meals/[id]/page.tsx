import { Meal } from "@/types/meal";
import apiClient from "@/config/api";
import Image from "next/image";
import { notFound } from "next/navigation";

async function getMealById(id: string): Promise<Meal| null> {
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
    <div className="max-w-6xl mx-auto p-6 bg-gray-50 shadow-lg rounded-lg text-gray-900">
      <h1 className="text-4xl font-extrabold text-burgundy-800 mb-4">
        {meal.name}
      </h1>
      <p className="text-gray-700 text-lg mb-6">{meal.description}</p>

      {meal.image && (
        <div className="mb-6">
          <Image
            src={meal.image}
            alt={meal.name}
            className="w-full h-auto rounded-lg shadow-md"
            width={1200}
            height={800}
          />
        </div>
      )}

      {meal.recipes.map((recipe, idx) => (
        <div
          key={idx}
          className="mb-10 p-6 bg-gray-100 rounded-lg shadow-md border-l-4 border-burgundy-700"
        >
          <h2 className="text-2xl font-semibold text-burgundy-700 mb-2">
            {recipe.name}
          </h2>
          <p className="text-gray-600 mb-4">{recipe.description}</p>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div className="p-4 bg-gray-100 border-l-4 border-burgundy-500 rounded shadow">
              <h3 className="text-xl font-medium text-gray-900 mb-2">
                Ingredientes
              </h3>
              <ul className="list-disc list-inside text-gray-700">
                {recipe.ingredients.map((ingredient, iIndex) => (
                  <li key={iIndex}>
                    <span className="font-semibold">
                      {ingredient.quantity} {ingredient.unit}
                    </span>{" "}
                    de {ingredient.name}
                  </li>
                ))}
              </ul>
            </div>

            <div className="p-4 bg-gray-100 border-l-4 border-burgundy-700 rounded shadow">
              <h3 className="text-xl font-medium text-gray-900 mb-2">
                Instrucciones
              </h3>
              <ol className="list-decimal list-inside space-y-2 text-gray-700">
                {recipe.instructions.map((step, sIndex) => (
                  <li
                    key={sIndex}
                    className="bg-burgundy-50 p-2 rounded-md shadow-sm"
                  >
                    {step}
                  </li>
                ))}
              </ol>
            </div>
          </div>
        </div>
      ))}
    </div>
  );
}