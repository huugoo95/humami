package com.hugo.humami.service.impl;

import com.hugo.humami.domain.Recipe;
import com.hugo.humami.dto.RecipeRequest;
import com.hugo.humami.dto.RecipeResponse;
import com.hugo.humami.repository.RecipeRepository;
import com.hugo.humami.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RecipeServiceImpl implements RecipeService {

    @Autowired
    RecipeRepository recipeRepository;

    @Override
    public List<RecipeResponse> getAll() {
        List<Recipe> recipes = recipeRepository.findAll();
        return recipes
                .stream()
                .map(it -> new RecipeResponse(it.getId(), it.getName(), it.getServings(), it.getIngredients(), it.getSteps()))
                .toList();
    }

    @Override
    public List<RecipeResponse> search(String search) {
        List<Recipe> recipes = recipeRepository.findAll();
        return recipes
                .stream()
                .map(it -> new RecipeResponse(it.getId(), it.getName(), it.getServings(), it.getIngredients(), it.getSteps()))
                .toList();
    }


    @Override
    public RecipeResponse getOne(String id) {
        Optional<Recipe> opRecipe = recipeRepository.findById(id);
        if (opRecipe.isEmpty()) {
            throw new ResourceNotFoundException();
        }

        return new RecipeResponse(opRecipe.get().getId(), opRecipe.get().getName(), opRecipe.get().getServings(), opRecipe.get().getIngredients(), opRecipe.get().getSteps());
    }

    @Override
    public void save(RecipeRequest recipeRequest) {
        Recipe recipe = new Recipe(recipeRequest.getName(), recipeRequest.getServings(), recipeRequest.getIngredients(), recipeRequest.getSteps(), recipeRequest.getTags());
        recipeRepository.save(recipe);
    }
}
