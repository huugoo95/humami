package com.hugo.humami.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class Recipe {
    private String id;
    private String name;
    private String description;
    private List<String> instructions;
    private List<InstructionStep> instructionSteps;
    private List<Ingredient> ingredients;

}