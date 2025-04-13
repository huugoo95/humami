package com.hugo.humami.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Ingredient {
    private String name;
    private Integer quantity;
    private String unit;
}