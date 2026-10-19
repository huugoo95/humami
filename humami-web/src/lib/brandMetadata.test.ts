import {
  BRAND_SOCIAL_IMAGE,
  blogPostMetadata,
  mealMetadata,
  mealsMetadata,
} from "./brandMetadata";

describe("brand metadata", () => {
  it("uses the versioned brand card for the recipe catalogue", () => {
    expect(BRAND_SOCIAL_IMAGE).toBe("/og-humami-v2.jpg");
    expect(mealsMetadata.alternates?.canonical).toBe("/meals");
    expect(mealsMetadata.openGraph?.url).toBe("/meals");
    expect(mealsMetadata.openGraph?.images).toEqual([
      expect.objectContaining({
        url: BRAND_SOCIAL_IMAGE,
        width: 1200,
        height: 630,
        alt: "Humami — Recetas y cocina con sabor auténtico",
      }),
    ]);
  });

  it("keeps an individual recipe URL and uses the brand card as fallback", () => {
    const metadata = mealMetadata({
      id: "tarta-queso",
      name: "Tarta de queso",
      description: "",
    });

    expect(metadata.alternates?.canonical).toBe("/meals/tarta-queso");
    expect(metadata.openGraph?.url).toBe("/meals/tarta-queso");
    expect(metadata.openGraph?.images).toEqual([
      expect.objectContaining({ url: BRAND_SOCIAL_IMAGE, alt: "Tarta de queso | Humami" }),
    ]);
  });

  it("uses a recipe image when it is provided", () => {
    const metadata = mealMetadata({
      id: "tarta-queso",
      name: "Tarta de queso",
      description: "Cremosa.",
      image: "https://img.humami.es/tarta.jpg",
    });

    expect(metadata.openGraph?.images).toEqual([
      expect.objectContaining({ url: "https://img.humami.es/tarta.jpg" }),
    ]);
  });

  it("shares a blog post with its cover image and SEO fields", () => {
    const metadata = blogPostMetadata({
      slug: "pan-casero",
      title: "Pan casero",
      excerpt: "Resumen.",
      seoTitle: "Cómo hacer pan casero",
      seoDescription: "Guía paso a paso.",
      coverImage: "https://img.humami.es/pan.jpg",
    });

    expect(metadata.title).toBe("Cómo hacer pan casero");
    expect(metadata.alternates?.canonical).toBe("/blog/pan-casero");
    expect(metadata.openGraph?.images).toEqual([expect.objectContaining({ url: "https://img.humami.es/pan.jpg" })]);
    expect(metadata.twitter?.images).toEqual(["https://img.humami.es/pan.jpg"]);
  });

  it("falls back to the brand card for a blog post without cover", () => {
    const metadata = blogPostMetadata({ slug: "notas", title: "Notas" });

    expect(metadata.description).toBe("Notas en el blog de Humami.");
    expect(metadata.openGraph?.images).toEqual([expect.objectContaining({ url: BRAND_SOCIAL_IMAGE })]);
  });
});
