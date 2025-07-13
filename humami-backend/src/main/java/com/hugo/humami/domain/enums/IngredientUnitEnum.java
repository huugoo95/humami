package com.hugo.humami.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
public enum IngredientUnitEnum {
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