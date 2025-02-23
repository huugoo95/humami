package com.hugo.humami.domain;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Document(collection = "recipes")
public class Recipe {
    @Id
    private String id;
    private String name;
    private int servings;
    private List<Ingredient> ingredients;
    private List<String> steps;
    private List<String> tags;

    public Recipe(String name, int servings, List<Ingredient> ingredients, List<String> steps, List<String> tags) {
        this.name = name;
        this.servings = servings;
        this.ingredients = ingredients;
        this.steps = steps;
        this.tags = tags;
    }
}


