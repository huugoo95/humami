package com.hugo.humami.service;

import com.hugo.humami.dto.RecipeRequest;
import com.hugo.humami.dto.RecipeResponse;

import java.util.List;

public interface RecipeService {

    List<RecipeResponse> getAll();
    List<RecipeResponse> search(String search);
    RecipeResponse getOne(String id);
    void save(RecipeRequest recipeRequest);

}
