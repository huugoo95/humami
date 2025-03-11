package com.hugo.humami.dto.request;

import lombok.Data;

@Data
public class IngredientRequest {
    private String name;
    private Integer quantity;
    private String unit;
}