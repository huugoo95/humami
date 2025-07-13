package com.hugo.humami.dto;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum IngredientUnitEnumDTO {
    TEASPOON("cucharadita"),
    TABLESPOON("cucharada"),
    MILLILITER("ml"),
    LITER("l"),
    CUP("taza"),
    GRAM("g"),
    KILOGRAM("kg"),
    MILLIGRAM("mg"),
    PIECE("pieza"),
    UNIT("unidad"),
    SLICE("rebanada"),
    LEAF("hoja"),
    CLOVE("diente"),
    PINCH("pizca"),
    HANDFUL("puñado"),
    TO_TASTE("al gusto"),
    DASH("chorro"),
    DROP("gotas");

    private final String label;
}