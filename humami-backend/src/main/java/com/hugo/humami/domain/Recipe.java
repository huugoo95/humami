package com.hugo.humami.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class Recipe {
    private String id;
    private String name;
    private String description;
    private List<String> instructions;
    private List<Ingredient> ingredients;
    private Integer prepTime;
    private String difficulty;
    private Integer portions;
}