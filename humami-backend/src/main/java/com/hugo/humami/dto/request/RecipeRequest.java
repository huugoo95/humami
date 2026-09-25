package com.hugo.humami.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
public class RecipeRequest {
    private String name;
    private String description;
    // legacy support during migration
    private List<String> instructions;
    private List<InstructionStepRequest> instructionSteps;
    private List<IngredientRequest> ingredients;
}