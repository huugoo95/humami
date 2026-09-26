import type { Metadata } from "next";
import { BRAND_SOCIAL_IMAGE } from "@/lib/brandMetadata";
import type { Guide } from "@/types/guide";

export function guideMetadata(guide: Guide): Metadata {
  const title = guide.seoTitle || guide.title;
  const description = guide.seoDescription || guide.excerpt;
  const image = guide.coverImage || BRAND_SOCIAL_IMAGE;
  const path = `/guias/${guide.slug}`;

  return {
    title,
    description,
    alternates: { canonical: path },
    openGraph: { type: "article", locale: "es_ES", url: path, siteName: "Humami", title, description, images: [{ url: image, alt: `Guía Humami: ${guide.title}` }] },
    twitter: { card: "summary_large_image", title, description, images: [image] },
  };
}
