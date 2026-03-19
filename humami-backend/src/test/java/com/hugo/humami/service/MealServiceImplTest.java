package com.hugo.humami.service;

import com.hugo.humami.domain.Ingredient;
import com.hugo.humami.domain.MealEntity;
import com.hugo.humami.domain.Recipe;
import com.hugo.humami.domain.enums.IngredientUnitEnum;
import com.hugo.humami.dto.response.IngredientResponse;
import com.hugo.humami.dto.response.MealResponse;
import com.hugo.humami.mapper.MealMapper;
import com.hugo.humami.repository.MealRepository;
import com.hugo.humami.service.impl.MealServiceImpl;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class MealServiceImplTest {

    @Test
    void searchShouldReturnTypoTolerantResultsFromFallback() {
        MealRepository mealRepository = mock(MealRepository.class);
        MealMapper mealMapper = mock(MealMapper.class);
        EmbeddingService embeddingService = mock(EmbeddingService.class);
        S3Service s3Service = mock(S3Service.class);

        MealServiceImpl service = new MealServiceImpl(mealRepository, mealMapper, embeddingService, s3Service);

        Ingredient ingredient = new Ingredient();
        ingredient.setName("cookies");

        Recipe recipe = new Recipe();
        recipe.setName("Salsa de cookies");
        recipe.setIngredients(List.of(ingredient));

        MealEntity cookiesMeal = new MealEntity();
        cookiesMeal.setId("meal-1");
        cookiesMeal.setName("Cookies caseras");
        cookiesMeal.setDescription("Galletas suaves estilo bakery");
        cookiesMeal.setRecipes(List.of(recipe));

        when(mealRepository.searchBySemanticText(anyString())).thenReturn(List.of());
        when(mealRepository.findAll()).thenReturn(List.of(cookiesMeal));

        MealResponse mapped = new MealResponse();
        mapped.setId("meal-1");
        mapped.setName("Cookies caseras");
        when(mealMapper.toResponse(cookiesMeal)).thenReturn(mapped);

        List<MealResponse> result = service.search("coockies");

        assertEquals(1, result.size());
        assertEquals("Cookies caseras", result.get(0).getName());
        verify(mealRepository).searchBySemanticText("coockies");
        verify(mealRepository).findAll();
    }

    @Test
    void getByIdShouldPopulateIngredientsByRecipeGrouped() throws Exception {
        MealRepository mealRepository = mock(MealRepository.class);
        MealMapper mealMapper = mock(MealMapper.class);
        EmbeddingService embeddingService = mock(EmbeddingService.class);
        S3Service s3Service = mock(S3Service.class);

        MealServiceImpl service = new MealServiceImpl(mealRepository, mealMapper, embeddingService, s3Service);

        Ingredient ingredient = new Ingredient();
        ingredient.setName("yogur griego");
        ingredient.setQuantity(new java.math.BigDecimal("200"));
        ingredient.setUnit(IngredientUnitEnum.GRAM);

        Recipe recipe = new Recipe();
        recipe.setName("Salsa de kebab");
        recipe.setDescription("Salsa cremosa para kebab");
        recipe.setIngredients(List.of(ingredient));

        MealEntity meal = new MealEntity();
        meal.setId("meal-1");
        meal.setName("Kebab");
        meal.setRecipes(List.of(recipe));

        MealResponse mealResponse = new MealResponse();
        mealResponse.setId("meal-1");
        mealResponse.setName("Kebab");

        IngredientResponse ingredientResponse = new IngredientResponse();
        ingredientResponse.setName("yogur griego");
        ingredientResponse.setQuantity(new java.math.BigDecimal("200"));
        ingredientResponse.setUnit("g");

        when(mealRepository.findById("meal-1")).thenReturn(Optional.of(meal));
        when(mealMapper.toResponse(meal)).thenReturn(mealResponse);
        when(mealMapper.toResponse(any(Ingredient.class))).thenReturn(ingredientResponse);

        MealResponse result = service.getById("meal-1");

        assertNotNull(result.getIngredientsByRecipe());
        assertEquals(1, result.getIngredientsByRecipe().size());
        assertEquals("Salsa de kebab", result.getIngredientsByRecipe().get(0).getRecipeName());
        assertEquals(1, result.getIngredientsByRecipe().get(0).getIngredients().size());
        assertEquals("yogur griego", result.getIngredientsByRecipe().get(0).getIngredients().get(0).getName());

        verify(mealRepository).findById("meal-1");
        verify(mealMapper).toResponse(meal);
        verify(mealMapper, atLeastOnce()).toResponse(any(Ingredient.class));
    }
}
