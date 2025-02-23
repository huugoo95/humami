package com.hugo.humami.dto;

import com.hugo.humami.domain.Ingredient;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
public class RecipeResponse {
    private String id;
    private String name;
    private int servings;
    private List<Ingredient> ingredients;
    private List<String> steps;

    public RecipeResponse(String id, String name, int servings, List<Ingredient> ingredients, List<String> steps) {
        this.id = id;
        this.name = name;
        this.servings = servings;
        this.ingredients = ingredients;
        this.steps = steps;
    }
}
