package com.hugo.humami.dto.response;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Data
public class AutocompleteResponse {
    private List<String> mealNames;
    private List<String> recipeTitles;

    public AutocompleteResponse(List<String> mealNames, List<String> recipeTitles) {
        this.mealNames = mealNames;
        this.recipeTitles = recipeTitles;
    }
}