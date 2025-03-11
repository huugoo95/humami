package com.hugo.humami.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "meals")
public class MealEntity {
    @Id
    private String id;
    private String name;
    private String description;
    private String image;
    private List<Recipe> recipes;
    private List<Double> embedding;
    private Instant createdAt;
    private Instant updatedAt;
}