package com.hugo.humami.domain.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum MealTypeEnum {
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