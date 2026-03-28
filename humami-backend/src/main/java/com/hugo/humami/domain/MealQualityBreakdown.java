package com.hugo.humami.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MealQualityBreakdown {
    private Double completeness;
    private Double specificity;
    private Double media;
}
