import { guideMetadata } from "@/features/guides/metadata";

describe("guideMetadata", () => {
  it("uses a guide-specific canonical URL and branded fallback image", () => {
    const metadata = guideMetadata({ id: "guide-1", slug: "pizza-napolitana-desde-cero", title: "Tu primera pizza napolitana en casa", excerpt: "Aprende a hacer pizza en casa." });
    expect(metadata.alternates).toEqual({ canonical: "/guias/pizza-napolitana-desde-cero" });
    expect(metadata.openGraph).toMatchObject({ title: "Tu primera pizza napolitana en casa", url: "/guias/pizza-napolitana-desde-cero" });
  });
});
