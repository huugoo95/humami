import { Metadata } from "next";
import { notFound } from "next/navigation";
import { GuideAccessForm } from "@/features/guides/GuideAccessForm";
import { getPublishedGuide } from "@/features/guides/api";
import { guideMetadata } from "@/features/guides/metadata";

type Props = { params: Promise<{ slug: string }> };

export async function generateMetadata({ params }: Props): Promise<Metadata> {
  const { slug } = await params;
  const guide = await getPublishedGuide(slug);
  return guide ? guideMetadata(guide) : {};
}

export default async function GuideLandingPage({ params }: Props) {
  const { slug } = await params;
  const guide = await getPublishedGuide(slug);
  if (!guide) notFound();
  return (
    <section className="mx-auto max-w-6xl">
      <div className="grid gap-10 lg:grid-cols-[1.05fr_0.95fr] lg:items-center">
        <div><p className="text-sm font-semibold uppercase tracking-[0.18em] text-humami-accent">Guía técnica · Humami</p><h1 className="mt-4 font-heading text-5xl leading-[0.98] text-humami-text-heading sm:text-6xl">{guide.title}</h1><p className="mt-6 max-w-xl text-lg leading-relaxed text-humami-text-base">{guide.excerpt}</p><dl className="mt-8 grid grid-cols-3 gap-4 border-y border-humami-text-base/10 py-5 text-sm"><div><dt className="text-humami-text-base/60">Nivel</dt><dd className="mt-1 font-semibold">{guide.level || "Todos los niveles"}</dd></div><div><dt className="text-humami-text-base/60">Tiempo</dt><dd className="mt-1 font-semibold">{guide.duration || "A tu ritmo"}</dd></div><div><dt className="text-humami-text-base/60">Formato</dt><dd className="mt-1 font-semibold">{guide.pageCount ? `${guide.pageCount} páginas` : "PDF"}</dd></div></dl></div>
        <div className="rounded-2xl bg-humami-accent-dark p-8 text-humami-bg shadow-xl sm:p-10"><p className="font-heading text-4xl leading-tight">{guide.landingHeadline || guide.title}</p><p className="mt-5 leading-relaxed text-humami-bg/85">{guide.landingDescription || guide.excerpt}</p><div className="mt-8 border-t border-humami-bg/20 pt-6 text-sm text-humami-bg/90">Acceso inmediato en la web · PDF en tu email</div></div>
      </div>
      <div className="mt-14 grid gap-10 lg:grid-cols-[1fr_1.1fr]"><div className="rounded-2xl border border-humami-text-base/10 bg-white/70 p-7"><p className="text-sm font-semibold uppercase tracking-[0.18em] text-humami-accent">Qué incluye</p><ul className="mt-5 space-y-4 text-humami-text-base">{(guide.contents || []).map((item) => <li key={item} className="flex gap-3"><span className="text-humami-gold">●</span><span>{item}</span></li>)}</ul></div><GuideAccessForm slug={guide.slug} /></div>
    </section>
  );
}
