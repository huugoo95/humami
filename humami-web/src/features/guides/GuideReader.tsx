"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { getGuideDocument } from "@/features/guides/api";

type Props = { slug: string; title: string };

export function GuideReader({ slug, title }: Props) {
  const [documentUrl, setDocumentUrl] = useState<string | null>(null);
  const [error, setError] = useState(false);
  useEffect(() => { getGuideDocument(slug).then((document) => setDocumentUrl(document.documentUrl)).catch(() => setError(true)); }, [slug]);
  if (error) return <section className="mx-auto max-w-2xl rounded-2xl border border-humami-text-base/10 bg-white/70 p-8 text-center shadow-sm"><p className="font-heading text-3xl text-humami-accent-dark">Necesitas acceso a esta guía</p><p className="mt-3 text-humami-text-base">Pide un nuevo enlace y podrás leerla aquí mismo.</p><Link href={`/guias/${slug}`} className="mt-6 inline-flex rounded-full bg-humami-accent-dark px-6 py-3 font-semibold text-humami-bg">Solicitar acceso</Link></section>;
  if (!documentUrl) return <p className="py-12 text-center text-humami-text-base">Preparando tu guía…</p>;
  const downloadName = slug === "pizza-napolitana-desde-cero"
    ? "guia-pizza-napolitana-humami.pdf"
    : `guia-${slug}-humami.pdf`;

  return <section className="mx-auto max-w-6xl"><div className="mb-6 flex flex-wrap items-end justify-between gap-4"><div><p className="text-sm font-semibold uppercase tracking-[0.18em] text-humami-accent">Guía Humami</p><h1 className="mt-2 font-heading text-4xl text-humami-text-heading sm:text-5xl">{title}</h1></div><a href={documentUrl} download={downloadName} className="inline-flex items-center gap-2 rounded-full bg-humami-accent-dark px-5 py-3 text-sm font-bold text-humami-bg shadow-sm transition hover:-translate-y-0.5 hover:bg-humami-accent focus:outline-none focus:ring-2 focus:ring-humami-gold focus:ring-offset-2"><svg aria-hidden="true" className="size-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2"><path strokeLinecap="round" strokeLinejoin="round" d="M12 3v12m0 0 4-4m-4 4-4-4m-5 6.5v1A2.5 2.5 0 0 0 5.5 21h13a2.5 2.5 0 0 0 2.5-2.5v-1" /></svg>Descargar guía PDF</a></div><iframe src={documentUrl} title={title} style={{ height: "78vh", minHeight: "620px" }} className="w-full rounded-xl border border-humami-text-base/15 bg-white shadow-sm" /></section>;
}
