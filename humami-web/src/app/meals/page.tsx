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
      const res = await apiClient.get(
        `/meals/search?query=${encodeURIComponent(query)}`
      );
      setMeals(res.data);
    } catch {
      setMeals([]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="px-3 sm:px-4 md:px-6 py-8">
      <div
        className={`
          w-full
          max-w-md        /* a tope de ancho en móvil */
          sm:max-w-7xl    /* más ancho a partir de sm */
          mx-auto
          bg-humami-bg-light
          shadow-lg
          rounded-lg
          p-3 sm:p-6      /* padding interior dinámico */
          text-humami-text-base
        `}
      >
        <h1 className="text-3xl sm:text-4xl font-extrabold text-humami-accent mb-4 sm:mb-6">
          Buscar Recetas
        </h1>

        <div className="flex flex-col sm:flex-row gap-3 sm:gap-4 mb-6">
          <input
            type="text"
            className="
              flex-1
              border border-gray-200
              rounded-lg
              px-3 py-2
              focus:outline-none focus:ring-2 focus:ring-humami-accent
            "
            placeholder="Ingresa nombre o descripción..."
            value={query}
            onChange={(e) => setQuery(e.target.value)}
          />
          <button
            onClick={handleSearch}
            className="
              w-full sm:w-auto
              px-5 sm:px-6 py-2.5
              bg-humami-accent text-humami-bg
              font-semibold rounded-lg shadow
              hover:bg-humami-accent-dark
              transition
            "
          >
            {loading ? "Buscando…" : "Buscar"}
          </button>
        </div>

        {loading ? (
          <p className="text-center text-humami-text-base">Cargando…</p>
        ) : meals.length > 0 ? (
          <div className="space-y-4">
            {meals.map((m) => (
              <div
                key={m.id}
                className="
                  bg-humami-bg
                  p-3 sm:p-4
                  rounded-lg shadow-md
                  border border-gray-200
                "
              >
                <Link href={`/meals/${m.id}`}>
                  <h2 className="text-xl sm:text-2xl font-semibold text-humami-accent hover:underline">
                    {m.name}
                  </h2>
                </Link>
                <p className="text-humami-text-base mt-1 sm:mt-2">
                  {m.description}
                </p>
              </div>
            ))}
          </div>
        ) : (
          <p className="text-center text-humami-text-base">
            No se encontraron recetas.
          </p>
        )}
      </div>
    </div>
  );
}