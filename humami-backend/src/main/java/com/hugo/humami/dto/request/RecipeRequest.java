package com.hugo.humami.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
public class RecipeRequest {
    private String name;
    private String description;
    private List<String> instructions;
    private List<IngredientRequest> ingredients;
    private Integer prepTime;
    private String difficulty;
    private Integer portions;
}