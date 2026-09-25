package com.hugo.humami.dto.request;

import com.hugo.humami.dto.IngredientUnitEnumDTO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class IngredientRequest {
    private String name;
    private BigDecimal quantity;
    private IngredientUnitEnumDTO unit;
    private String link;
    private Boolean isOptional;
}