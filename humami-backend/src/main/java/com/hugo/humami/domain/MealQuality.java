package com.hugo.humami.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class MealQuality {
    private Double score;
    private Integer version;
    private MealQualityBreakdown breakdown;
    private List<String> flags;
}
