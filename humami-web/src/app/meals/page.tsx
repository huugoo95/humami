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

const SCROLL_KEY = "humami:meals:scrollY";
const ANCHOR_KEY = "humami:meals:anchorId";

export default function MealsPage() {
  const [query, setQuery] = useState("");
  const [submittedQuery, setSubmittedQuery] = useState("");
  const [meals, setMeals] = useState<Meal[]>([]);
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [initialized, setInitialized] = useState(false);

  const updateUrlState = (nextPage: number, nextQuery: string) => {
    if (typeof window === "undefined") return;

    const params = new URLSearchParams(window.location.search);

    if (nextPage > 1) params.set("page", String(nextPage));
    else params.delete("page");

    if (nextQuery.trim()) params.set("query", nextQuery.trim());
    else params.delete("query");

    const qs = params.toString();
    const nextUrl = qs ? `${window.location.pathname}?${qs}` : window.location.pathname;
    window.history.replaceState({}, "", nextUrl);
  };

  const loadMeals = async (nextPage: number, nextQuery: string) => {
    setLoading(true);
    try {
      const res = await apiClient.get<MealsPageResponse>(
        `/meals?query=${encodeURIComponent(nextQuery)}&page=${nextPage}&limit=12`
      );
      setMeals(res.data.items || []);
      setPage(res.data.page || 1);
      setTotalPages(Math.max(res.data.totalPages || 1, 1));
      updateUrlState(res.data.page || 1, nextQuery);
    } catch {
      setMeals([]);
      setTotalPages(1);
      updateUrlState(1, nextQuery);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (initialized) return;

    const params = new URLSearchParams(window.location.search);
    const initialQuery = (params.get("query") || "").trim();
    const pageParam = Number(params.get("page") || "1");
    const initialPage = Number.isFinite(pageParam) && pageParam > 0 ? pageParam : 1;

    setQuery(initialQuery);
    setSubmittedQuery(initialQuery);
    setInitialized(true);
    loadMeals(initialPage, initialQuery);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [initialized]);

  useEffect(() => {
    if (loading || meals.length === 0) return;

    const anchorId = sessionStorage.getItem(ANCHOR_KEY);
    const scrollY = sessionStorage.getItem(SCROLL_KEY);

    if (anchorId) {
      const el = document.getElementById(`meal-card-${anchorId}`);
      if (el) {
        el.scrollIntoView({ block: "center", behavior: "auto" });
      }
      sessionStorage.removeItem(ANCHOR_KEY);
      sessionStorage.removeItem(SCROLL_KEY);
      return;
    }

    if (scrollY) {
      const y = Number(scrollY);
      if (Number.isFinite(y)) {
        window.scrollTo({ top: y, behavior: "auto" });
      }
      sessionStorage.removeItem(SCROLL_KEY);
    }
  }, [loading, meals]);

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
                  id={`meal-card-${m.id}`}
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

                  <Link
                    href={`/meals/${m.id}`}
                    onClick={() => {
                      sessionStorage.setItem(SCROLL_KEY, String(window.scrollY));
                      sessionStorage.setItem(ANCHOR_KEY, m.id);
                    }}
                  >
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
