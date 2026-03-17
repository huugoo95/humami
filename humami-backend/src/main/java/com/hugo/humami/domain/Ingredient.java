package com.hugo.humami.domain;

import com.hugo.humami.domain.enums.IngredientUnitEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class Ingredient {
    private String name;
    private BigDecimal quantity;
    private IngredientUnitEnum unit;
    private String link;
    private boolean isOptional;

}