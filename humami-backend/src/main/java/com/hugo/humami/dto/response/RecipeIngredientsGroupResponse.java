package com.hugo.humami.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class RecipeIngredientsGroupResponse {
    private String recipeName;
    private String recipeDescription;
    private List<IngredientResponse> ingredients;
}
