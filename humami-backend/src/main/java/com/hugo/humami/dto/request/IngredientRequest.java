package com.hugo.humami.dto.request;

import com.hugo.humami.dto.IngredientUnitEnumDTO;
import lombok.Data;

@Data
public class IngredientRequest {
    private String name;
    private Integer quantity;
    private IngredientUnitEnumDTO unit;
    private String link;
}