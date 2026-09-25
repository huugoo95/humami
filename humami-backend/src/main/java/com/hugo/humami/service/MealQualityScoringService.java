package com.hugo.humami.service;

import com.hugo.humami.domain.MealEntity;
import com.hugo.humami.domain.MealQuality;

public interface MealQualityScoringService {
    MealQuality score(MealEntity mealEntity);
}
