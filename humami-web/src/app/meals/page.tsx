"use client";

import { useState } from "react";
import Link from "next/link";
import apiClient from "@/config/api";
import { Meal } from "@/types/meal";

export default function MealsPage() {
  const [query, setQuery] = useState("");
  const [meals, setMeals] = useState<Meal[]>([]);
  const [loading, setLoading] = useState(false);

  const handleSearch = async () => {
    setLoading(true);
    try {
      const response = await apiClient.get(
        `/meals/search?search=${encodeURIComponent(query)}`
      );
      setMeals(response.data);
    } catch (error) {
      console.error("Error al buscar menús:", error);
      setMeals([]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-6xl mx-auto p-6 bg-gray-50 shadow-lg rounded-lg text-gray-900">
      <h1 className="text-4xl font-extrabold text-burgundy-800 mb-6">
        Buscar Menús
      </h1>

      {/* Barra de búsqueda */}
      <div className="mb-6 flex gap-4">
        <input
          type="text"
          className="border border-gray-300 p-3 flex-1 rounded-lg shadow-sm"
          placeholder="Ingresa el nombre o descripción del menú..."
          value={query}
          onChange={(e) => setQuery(e.target.value)}
        />
        <button
          className="px-6 py-3 text-burgundy-700 font-semibold rounded-lg shadow hover:bg-burgundy-800 transition"
          onClick={handleSearch}
        >
          Buscar
        </button>
      </div>

      {/* Resultado de búsqueda */}
      {loading ? (
        <p className="text-gray-700 text-lg">Cargando...</p>
      ) : meals.length > 0 ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {meals.map((meal) => (
            <div
              key={meal.id}
              className="bg-white p-4 rounded-lg shadow-md border border-gray-200"
            >
              <Link href={`/meals/${meal.id}`}>
                <h2 className="text-2xl font-semibold text-burgundy-700 hover:underline cursor-pointer">
                  {meal.name}
                </h2>
              </Link>
              <p className="text-gray-600 mt-2">{meal.description}</p>
            </div>
          ))}
        </div>
      ) : (
        !loading && <p className="text-gray-700 text-lg">No se encontraron menús.</p>
      )}
    </div>
  );
}