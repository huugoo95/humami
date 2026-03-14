"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import Image from "next/image";
import apiClient from "@/config/api";
import { Meal } from "@/types/meal";

type MealsPageResponse = {
  items: Meal[];
  page: number;
  limit: number;
  totalItems: number;
  totalPages: number;
};

export default function MealsPage() {
  const [query, setQuery] = useState("");
  const [submittedQuery, setSubmittedQuery] = useState("");
  const [meals, setMeals] = useState<Meal[]>([]);
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);

  const loadMeals = async (nextPage: number, nextQuery: string) => {
    setLoading(true);
    try {
      const res = await apiClient.get<MealsPageResponse>(
        `/meals?query=${encodeURIComponent(nextQuery)}&page=${nextPage}&limit=12`
      );
      setMeals(res.data.items || []);
      setPage(res.data.page || 1);
      setTotalPages(Math.max(res.data.totalPages || 1, 1));
    } catch {
      setMeals([]);
      setTotalPages(1);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadMeals(1, "");
  }, []);

  const handleSearch = () => {
    const normalized = query.trim();
    setSubmittedQuery(normalized);
    loadMeals(1, normalized);
  };

  const goToPage = (nextPage: number) => {
    if (nextPage < 1 || nextPage > totalPages || nextPage === page) return;
    loadMeals(nextPage, submittedQuery);
  };

  return (
    <div className="px-3 sm:px-4 md:px-6 py-8">
      <div
        className={`
          w-full
          max-w-md
          sm:max-w-7xl
          mx-auto
          bg-humami-bg-light
          shadow-lg
          rounded-lg
          p-3 sm:p-6
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
            onKeyDown={(e) => {
              if (e.key === "Enter") handleSearch();
            }}
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
          <>
            <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-4">
              {meals.map((m) => (
                <div
                  key={m.id}
                  className="
                    bg-humami-bg
                    p-3 sm:p-4
                    rounded-lg shadow-md
                    border border-gray-200
                    h-full
                    flex flex-col
                  "
                >
                  {m.image ? (
                    <div className="mb-3">
                      <Image
                        src={m.image}
                        alt={m.name}
                        width={640}
                        height={360}
                        className="w-full h-auto rounded-md object-cover"
                      />
                    </div>
                  ) : (
                    <div className="mb-3 w-full aspect-video rounded-md bg-gray-100 flex items-center justify-center text-gray-400 text-sm">
                      Sin imagen
                    </div>
                  )}

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

            <div className="mt-6 flex items-center justify-center gap-3">
              <button
                onClick={() => goToPage(page - 1)}
                disabled={page <= 1}
                className="px-4 py-2 rounded border border-gray-300 disabled:opacity-40"
              >
                Anterior
              </button>
              <span className="text-sm text-gray-700">
                Página {page} de {totalPages}
              </span>
              <button
                onClick={() => goToPage(page + 1)}
                disabled={page >= totalPages}
                className="px-4 py-2 rounded border border-gray-300 disabled:opacity-40"
              >
                Siguiente
              </button>
            </div>
          </>
        ) : (
          <p className="text-center text-humami-text-base">
            No se encontraron recetas.
          </p>
        )}
      </div>
    </div>
  );
}
