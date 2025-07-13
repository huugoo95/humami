package com.hugo.humami.domain;

import com.hugo.humami.domain.enums.IngredientUnitEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Ingredient {
    private String name;
    private Integer quantity;
    private IngredientUnitEnum unit;
    private String link;

}