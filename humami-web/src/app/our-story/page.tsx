import Link from "next/link";
import apiClient from "@/config/api";

type AboutPayload = {
  title: string;
  story: string[];
  photoUrl: string;
  updatedAt: string;
};

const FALLBACK_ABOUT: AboutPayload = {
  title: "La historia detrás de Humami",
  story: [
    "Humami nace de una mezcla muy personal: la cabeza de un developer y el corazón de una cocina de casa.",
    "Detrás del proyecto está Hugo, hijo de profesora de cocina, criado entre recetas, producto y respeto por el oficio.",
    "Desde una raíz gallega, Humami une método y sabor para cocinar mejor cada día.",
  ],
  photoUrl: "",
  updatedAt: "",
};

async function getAbout(): Promise<AboutPayload> {
  try {
    const response = await apiClient.get<AboutPayload>("/about");
    return {
      ...FALLBACK_ABOUT,
      ...response.data,
      story:
        Array.isArray(response.data?.story) && response.data.story.length > 0
          ? response.data.story
          : FALLBACK_ABOUT.story,
    };
  } catch (error) {
    console.error("Error loading about data", error);
    return FALLBACK_ABOUT;
  }
}

export default async function OurStoryPage() {
  const about = await getAbout();

  return (
    <main className="mx-auto max-w-4xl py-8 md:py-12">
      <section className="rounded-2xl border border-humami-text-base/10 bg-white/70 p-6 md:p-10 shadow-sm">
        <p className="text-sm font-semibold tracking-wider uppercase text-humami-accent-dark mb-3">Sobre nosotros</p>

        <h1 className="text-3xl md:text-5xl font-heading text-humami-text-heading mb-6">{about.title}</h1>

        <div className="space-y-5 text-base md:text-lg leading-relaxed font-body text-humami-text-base">
          {about.story.map((line, index) => (
            <p key={index}>{line}</p>
          ))}
        </div>

        {about.photoUrl ? (
          <figure className="mt-8">
            <img
              src={about.photoUrl}
              alt="Foto de Hugo"
              className="w-full max-w-sm rounded-2xl border border-humami-text-base/15 shadow-sm object-cover"
              loading="lazy"
            />
            <figcaption className="mt-2 text-sm text-humami-text-base/70">
              Foto servida desde origen externo (S3/CDN).
            </figcaption>
          </figure>
        ) : (
          <div className="mt-8 rounded-xl border border-dashed border-humami-text-base/25 p-4 md:p-5 bg-humami-bg-light/50">
            <p className="text-sm md:text-base font-body text-humami-text-base/85">
              Sin foto configurada todavía. Cuando definas `photoUrl` en `/api/about`, aparecerá aquí automáticamente.
            </p>
          </div>
        )}

        <div className="mt-8 flex flex-wrap gap-3">
          <Link
            href="/meals"
            className="inline-block px-5 py-3 font-medium rounded-md bg-humami-accent-dark text-humami-bg transition hover:bg-humami-accent-dark"
          >
            Ver comidas
          </Link>
          <Link
            href="/"
            className="inline-block px-5 py-3 font-medium rounded-md border border-humami-accent-dark text-humami-accent-dark transition hover:bg-humami-accent-dark/5"
          >
            Volver al inicio
          </Link>
        </div>
      </section>
    </main>
  );
}
