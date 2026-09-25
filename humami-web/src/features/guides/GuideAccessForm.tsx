"use client";

import { FormEvent, useState } from "react";
import { useRouter } from "next/navigation";
import { requestGuideAccess } from "@/features/guides/api";

type Props = { slug: string };
const PRIVACY_NOTICE_VERSION = "2026-09";

export function GuideAccessForm({ slug }: Props) {
  const router = useRouter();
  const [email, setEmail] = useState("");
  const [privacyAccepted, setPrivacyAccepted] = useState(false);
  const [marketingOptIn, setMarketingOptIn] = useState(false);
  const [website, setWebsite] = useState("");
  const [error, setError] = useState("");
  const [submitting, setSubmitting] = useState(false);

  async function onSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError("");
    if (!privacyAccepted) {
      setError("Necesitamos que aceptes la política de privacidad para enviarte la guía.");
      return;
    }
    setSubmitting(true);
    try {
      const access = await requestGuideAccess(slug, { email, privacyNoticeVersion: PRIVACY_NOTICE_VERSION, privacyAccepted, marketingOptIn, source: "guide-landing", website });
      router.push(access.readerUrl);
    } catch (requestError) {
      setError(requestError instanceof Error ? requestError.message : "No hemos podido darte acceso ahora mismo.");
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <form onSubmit={onSubmit} className="rounded-2xl bg-humami-accent-dark p-6 text-humami-bg shadow-lg sm:p-8">
      <p className="font-heading text-3xl">Lee la guía ahora</p>
      <p className="mt-2 max-w-xl text-sm leading-relaxed text-humami-bg/85">Déjanos tu email y se abrirá aquí mismo. También te enviaremos un enlace para volver cuando quieras.</p>
      <label className="mt-5 block text-sm font-semibold" htmlFor="guide-email">Email</label>
      <input id="guide-email" name="email" type="email" value={email} onChange={(event) => setEmail(event.target.value)} required autoComplete="email" className="mt-2 w-full rounded-lg border border-humami-bg/30 bg-white px-4 py-3 text-humami-text-base outline-none ring-humami-gold focus:ring-2" placeholder="tu@email.com" />
      <label className="mt-4 flex cursor-pointer items-start gap-3 text-sm leading-relaxed">
        <input type="checkbox" checked={privacyAccepted} onChange={(event) => setPrivacyAccepted(event.target.checked)} className="mt-1 size-4 accent-humami-gold" />
        <span>He leído y acepto la <a className="underline underline-offset-2" href="/privacidad">política de privacidad</a> para recibir esta guía.</span>
      </label>
      <label className="mt-3 flex cursor-pointer items-start gap-3 text-sm leading-relaxed text-humami-bg/85">
        <input type="checkbox" checked={marketingOptIn} onChange={(event) => setMarketingOptIn(event.target.checked)} className="mt-1 size-4 accent-humami-gold" />
        <span>Quiero recibir ideas y nuevas guías de Humami por email.</span>
      </label>
      <div aria-hidden="true" style={{ position: "absolute", width: 1, height: 1, overflow: "hidden", clip: "rect(0 0 0 0)" }}>
        <input
          name="website"
          value={website}
          onChange={(event) => setWebsite(event.target.value)}
          tabIndex={-1}
          autoComplete="off"
        />
      </div>
      {error && <p role="alert" className="mt-4 rounded-lg bg-white/10 p-3 text-sm">{error}</p>}
      <button type="submit" disabled={submitting} className="mt-6 rounded-full bg-humami-gold px-6 py-3 text-sm font-bold text-humami-accent-dark transition hover:bg-humami-bg disabled:cursor-not-allowed disabled:opacity-70">
        {submitting ? "Abriendo la guía…" : "Ver la guía ahora"}
      </button>
    </form>
  );
}
