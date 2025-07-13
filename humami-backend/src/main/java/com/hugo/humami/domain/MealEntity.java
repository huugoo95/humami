package com.hugo.humami.domain;

import com.hugo.humami.domain.enums.MealTypeEnum;
import com.hugo.humami.dto.request.TimingsRequest;
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
    private TimingsRequest timings;
    private MealTypeEnum type;
    private String difficulty;
    private Integer servings;
    private List<Faq> faqs;
    private Instant createdAt;
    private Instant updatedAt;

    public boolean hasImage(){
        return (this.getImage() != null);
    }
}