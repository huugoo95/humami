package com.hugo.humami.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

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

    @Getter
    private final String label;
}