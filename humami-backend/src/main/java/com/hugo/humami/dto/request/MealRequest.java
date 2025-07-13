package com.hugo.humami.dto.request;

import com.hugo.humami.dto.MealTypeEnumDTO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
public class MealRequest {
    private String name;
    private String description;
    private List<RecipeRequest> recipes;
    private TimingsRequest timings;
    private DifficultyEnumRequest difficulty;
    private MealTypeEnumDTO type;
    private Integer servings;
    private List<FaqRequest> faqs;
}
