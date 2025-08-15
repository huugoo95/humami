package com.hugo.humami.mapper;

import com.hugo.humami.domain.*;
import com.hugo.humami.domain.enums.MealTypeEnum;
import com.hugo.humami.dto.MealTypeEnumDTO;
import com.hugo.humami.dto.request.*;
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

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(source = "type", target = "type")
    @Mapping(source = "timings", target = "timings")
    @Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.Instant.now())")
    MealEntity toEntity(MealRequest mealRequest);

    void updateMealFromRequest(@MappingTarget MealEntity target, MealRequest source);
    MealResponse toResponse(MealEntity mealEntity);

    MealResponse toTinyResponse(MealEntity mealEntity);
    Recipe toEntity(RecipeRequest request);
    Ingredient toEntity(IngredientRequest request);
    Faq toEntity(FaqRequest request);
    Timing toEntity(TimingsRequest request);

    RecipeResponse toResponse(Recipe recipe);

    @Mapping(target = "unit",
            expression = "java(ingredient.getUnit() != null ? ingredient.getUnit().getLabel() : null)")
    IngredientResponse toResponse(Ingredient ingredient);

    default MealTypeEnum map(MealTypeEnumDTO dto) {
        return dto == null ? null : MealTypeEnum.valueOf(dto.name());
    }
    default MealTypeEnumDTO map(MealTypeEnum entity) {
        return entity == null ? null : MealTypeEnumDTO.valueOf(entity.name());
    }
}