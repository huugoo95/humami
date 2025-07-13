package com.hugo.humami.dto.response;

import com.hugo.humami.dto.request.FaqRequest;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
public class RecipeResponse {
    private String name;
    private String description;
    private List<String> instructions;
    private List<IngredientResponse> ingredients;

}