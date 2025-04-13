package com.hugo.humami.dto.request;

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
    private String image;
    private List<RecipeRequest> recipes;
}
