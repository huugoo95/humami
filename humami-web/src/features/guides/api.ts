import { API_BASE_URL } from "@/config/api";
import { getDevelopmentGuides } from "@/features/guides/localGuides";
import type { Guide, GuideAccessRequest, GuideAccessResponse, GuideDocumentResponse } from "@/types/guide";

const GUIDE_API = "/guides";

function isLocalPreview(): boolean {
  return typeof window !== "undefined"
    && window.location.hostname === "localhost"
    && process.env.NODE_ENV === "development";
}

function apiUrl(path: string): string | null {
  if (!API_BASE_URL) return null;
  return `${API_BASE_URL}${path}`;
}

export async function getPublishedGuides(): Promise<Guide[]> {
  const url = apiUrl(GUIDE_API);
  if (!url) return getDevelopmentGuides();

  try {
    const response = await fetch(url, { next: { revalidate: 300 } });
    if (!response.ok) return [];
    return (await response.json()) as Guide[];
  } catch {
    return [];
  }
}

export async function getPublishedGuide(slug: string): Promise<Guide | null> {
  const url = apiUrl(`${GUIDE_API}/${slug}`);
  if (!url) return getDevelopmentGuides().find((guide) => guide.slug === slug) ?? null;

  try {
    const response = await fetch(url, { next: { revalidate: 300 } });
    if (!response.ok) return null;
    return (await response.json()) as Guide;
  } catch {
    return null;
  }
}

export async function requestGuideAccess(slug: string, request: GuideAccessRequest): Promise<GuideAccessResponse> {
  const url = apiUrl(`${GUIDE_API}/${slug}/access`);
  if (isLocalPreview() || (!url && process.env.NODE_ENV === "development")) return { readerUrl: `/guias/${slug}/leer?preview=1` };
  if (!url) throw new Error("El acceso a guías no está disponible.");

  const response = await fetch(url, {
    method: "POST",
    credentials: "include",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(request),
  });
  if (!response.ok) throw new Error("No hemos podido darte acceso ahora mismo.");
  return (await response.json()) as GuideAccessResponse;
}

export async function getGuideDocument(slug: string): Promise<GuideDocumentResponse> {
  const url = apiUrl(`${GUIDE_API}/${slug}/document`);
  const previewUrl = process.env.NEXT_PUBLIC_GUIDE_PREVIEW_PDF_URL;
  if ((isLocalPreview() || !url) && previewUrl && process.env.NODE_ENV === "development") return { documentUrl: previewUrl };
  if (!url) throw new Error("No tienes acceso a esta guía.");

  const response = await fetch(url, { credentials: "include" });
  if (!response.ok) throw new Error("No tienes acceso a esta guía.");
  return (await response.json()) as GuideDocumentResponse;
}
