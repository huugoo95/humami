import {
  buildRecipeJsonLd,
  formatDuration,
  formatIngredientAmount,
  formatQuantity,
  ingredientLine,
  splitLinks,
  toIsoDuration,
} from "./recipeFormat";

describe("formatQuantity", () => {
  it("uses fractions for common halves and quarters", () => {
    expect(formatQuantity(0.5)).toBe("½");
    expect(formatQuantity(1.5)).toBe("1½");
    expect(formatQuantity(2.25)).toBe("2¼");
  });

  it("uses a decimal comma otherwise", () => {
    expect(formatQuantity(0.3)).toBe("0,3");
    expect(formatQuantity(750)).toBe("750");
  });
});

describe("formatIngredientAmount", () => {
  it("pluralises every unit label the API returns", () => {
    expect(formatIngredientAmount(2, "pieza")).toBe("2 piezas");
    expect(formatIngredientAmount(3, "pizca")).toBe("3 pizcas");
    expect(formatIngredientAmount(2, "puñado")).toBe("2 puñados");
    expect(formatIngredientAmount(4, "diente")).toBe("4 dientes");
  });

  it("keeps fractional amounts singular", () => {
    expect(formatIngredientAmount(0.5, "cucharadita")).toBe("½ cucharadita");
    expect(formatIngredientAmount(1.5, "cucharadita")).toBe("1½ cucharaditas");
  });

  it("shows counted units as a bare number", () => {
    expect(formatIngredientAmount(2, "unidad")).toBe("2");
  });

  it("shows 'al gusto' without a quantity", () => {
    expect(formatIngredientAmount(null, "al gusto")).toBe("al gusto");
    expect(formatIngredientAmount(undefined, undefined)).toBeNull();
  });

  it("does not pluralise symbols", () => {
    expect(formatIngredientAmount(500, "g")).toBe("500 g");
    expect(formatIngredientAmount(1.5, "kg")).toBe("1½ kg");
  });
});

describe("durations", () => {
  it("formats hours for people", () => {
    expect(formatDuration(0.75)).toBe("45 min");
    expect(formatDuration(2)).toBe("2 h");
    expect(formatDuration(3.5)).toBe("3 h 30 min");
    expect(formatDuration(0)).toBeNull();
  });

  it("formats hours as ISO 8601", () => {
    expect(toIsoDuration(3.5)).toBe("PT3H30M");
    expect(toIsoDuration(0.75)).toBe("PT45M");
    expect(toIsoDuration(null)).toBeUndefined();
  });
});

describe("ingredientLine", () => {
  it("reads naturally for structured data", () => {
    expect(ingredientLine({ name: "Sal", quantity: null, unit: "al gusto" })).toBe("Sal, al gusto");
    expect(ingredientLine({ name: "Ajo", quantity: 3, unit: "diente" })).toBe("3 dientes Ajo");
  });
});

describe("splitLinks", () => {
  it("turns bare YouTube references into links", () => {
    const parts = splitLinks("En el vídeo de Diego Doal (youtube.com/watch?v=4FvvPec0kCE), que recomendamos.");
    expect(parts).toEqual([
      { text: "En el vídeo de Diego Doal (" },
      { text: "youtube.com/watch?v=4FvvPec0kCE", href: "https://youtube.com/watch?v=4FvvPec0kCE" },
      { text: "), que recomendamos." },
    ]);
  });

  it("leaves plain text alone", () => {
    expect(splitLinks("Sin enlaces. Fin.")).toEqual([{ text: "Sin enlaces. Fin." }]);
  });
});

describe("buildRecipeJsonLd", () => {
  it("describes a multi-preparation meal with sections, times and FAQs", () => {
    const [recipe, faq] = buildRecipeJsonLd(
      {
        id: "abc",
        name: "Tacos",
        description: "Ricos.",
        image: "https://img.humami.es/t.jpg",
        servings: 6,
        timings: { prepTimeInHours: 0.75, totalTimeInHours: 5.5 },
        recipes: [
          { name: "Carne", ingredients: [{ name: "Carrilleras", quantity: 1.5, unit: "kg" }], instructionSteps: [{ order: 2, text: "B" }, { order: 1, text: "A" }] },
          { name: "Salsa", ingredients: [{ name: "Sal", quantity: null, unit: "al gusto" }], instructions: ["C"] },
        ],
        faqs: [{ question: "¿Pica?", answer: "Poco." }],
      },
      "https://humami.es",
      "principal",
    );

    expect(recipe).toMatchObject({
      "@type": "Recipe",
      url: "https://humami.es/meals/abc",
      prepTime: "PT45M",
      totalTime: "PT5H30M",
      recipeYield: "6 raciones",
      recipeIngredient: ["1½ kg Carrilleras", "Sal, al gusto"],
    });
    expect((recipe.recipeInstructions as Array<{ name: string; itemListElement: Array<{ text: string }> }>)[0]).toMatchObject({
      "@type": "HowToSection",
      name: "Carne",
      itemListElement: [{ text: "A" }, { text: "B" }],
    });
    expect(faq).toMatchObject({ "@type": "FAQPage", mainEntity: [{ name: "¿Pica?" }] });
  });
});
