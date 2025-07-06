package com.hugo.humami.dto.response;

import com.hugo.humami.domain.Recipe;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Getter
@NoArgsConstructor
@Data
public class MealResponse {
    private String id;
    private String name;
    private String description;
    private String image;
    private List<Recipe> recipes;
    private Instant createdAt;
    private Instant updatedAt;
}