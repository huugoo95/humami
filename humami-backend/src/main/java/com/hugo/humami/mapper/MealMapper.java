package com.hugo.humami.mapper;

import com.hugo.humami.domain.Ingredient;
import com.hugo.humami.domain.MealEntity;
import com.hugo.humami.domain.Recipe;
import com.hugo.humami.dto.request.IngredientRequest;
import com.hugo.humami.dto.request.MealRequest;
import com.hugo.humami.dto.request.RecipeRequest;
import com.hugo.humami.dto.response.IngredientResponse;
import com.hugo.humami.dto.response.MealResponse;
import com.hugo.humami.dto.response.RecipeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring")
public interface MealMapper {
    MealMapper INSTANCE = Mappers.getMapper(MealMapper.class);
    @Mapping(target = "id", ignore = true) // El ID lo genera la base de datos
    @Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.Instant.now())")
    MealEntity toEntity(MealRequest mealRequest);

    void updateMealFromRequest(@MappingTarget MealEntity target, MealRequest source);
    MealResponse toResponse(MealEntity mealEntity);

    Recipe toEntity(RecipeRequest recipeRequest);

    RecipeResponse toResponse(Recipe recipe);

    Ingredient toEntity(IngredientRequest ingredientRequest);

    IngredientResponse toResponse(Ingredient ingredient);

}