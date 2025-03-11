package com.hugo.humami.dto.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
public class RecipeResponse {
    private String name;
    private String description;
    private List<String> instructions;
    private List<IngredientResponse> ingredients;
    private Integer prepTime;
    private String difficulty;
    private Integer portions;

}