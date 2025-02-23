package com.hugo.humami.dto;

import com.hugo.humami.domain.Ingredient;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
public class RecipeRequest {
    private String name;
    private int servings;
    private List<Ingredient> ingredients;
    private List<String> steps;
    private List<String> tags;
}

