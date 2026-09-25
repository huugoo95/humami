import {
  BRAND_SOCIAL_IMAGE,
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
      image: "https://humami.s3.amazonaws.com/meals/tarta.jpg",
    });

    expect(metadata.openGraph?.images).toEqual([
      expect.objectContaining({ url: "https://humami.s3.amazonaws.com/meals/tarta.jpg" }),
    ]);
  });
});
