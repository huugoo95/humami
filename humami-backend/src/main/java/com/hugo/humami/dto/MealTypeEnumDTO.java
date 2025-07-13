package com.hugo.humami.dto;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum MealTypeEnumDTO {
    BREAKFAST("desayuno"),
    BRUNCH("brunch"),
    STARTER("entrante"),
    MAIN("plato principal"),
    SIDE("guarnición"),
    SAUCE("salsa"),
    DESSERT("postre"),
    SNACK("tentempié"),
    DRINK("bebida"),
    BREAD("pan"),
    SOUP("sopa");

    private final String label;
}