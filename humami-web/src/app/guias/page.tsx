import Link from "next/link";
import { getPublishedGuides } from "@/features/guides/api";

export const metadata = {
  title: "Guías",
  description: "Guías prácticas de Humami para cocinar mejor, con método y sin complicaciones.",
  alternates: { canonical: "/guias" },
};

export default async function GuidesIndexPage() {
  const guides = await getPublishedGuides();
  return (
    <section className="mx-auto max-w-6xl">
      <p className="text-sm font-semibold uppercase tracking-[0.18em] text-humami-accent">Humami</p>
      <h1 className="mt-3 font-heading text-5xl text-humami-text-heading sm:text-6xl">Guías</h1>
      <p className="mt-5 max-w-2xl text-lg leading-relaxed text-humami-text-base">Técnica, método y práctica para cocinar con más seguridad. Guías largas para las cosas que merecen hacerlas bien.</p>
      {guides.length === 0 ? <p className="mt-10 rounded-xl border border-humami-text-base/10 bg-white/70 p-6 text-humami-text-base">Aún no hay guías publicadas.</p> : <div className="mt-10 grid gap-6 md:grid-cols-2">{guides.map((guide) => <article key={guide.id} className="flex min-h-72 flex-col rounded-2xl border border-humami-text-base/10 bg-white/75 p-7 shadow-sm"><p className="text-sm font-semibold uppercase tracking-[0.18em] text-humami-accent">Guía técnica</p><h2 className="mt-4 font-heading text-4xl leading-tight text-humami-accent-dark">{guide.title}</h2><p className="mt-4 leading-relaxed text-humami-text-base">{guide.excerpt}</p><div className="mt-auto pt-6 text-sm text-humami-text-base/70">{[guide.level, guide.duration, guide.pageCount ? `${guide.pageCount} páginas` : undefined].filter(Boolean).join(" · ")}</div><Link href={`/guias/${guide.slug}`} className="mt-5 inline-flex w-fit rounded-full bg-humami-accent-dark px-5 py-2.5 text-sm font-semibold text-humami-bg hover:bg-humami-accent">Ver guía</Link></article>)}</div>}
    </section>
  );
}
