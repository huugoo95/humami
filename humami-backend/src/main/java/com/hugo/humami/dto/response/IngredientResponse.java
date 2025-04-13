package com.hugo.humami.dto.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class IngredientResponse {
    private String name;
    private Integer quantity;
    private String unit;

}