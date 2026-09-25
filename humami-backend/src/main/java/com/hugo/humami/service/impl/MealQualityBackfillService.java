package com.hugo.humami.service.impl;

import com.hugo.humami.domain.MealEntity;
import com.hugo.humami.repository.MealRepository;
import com.hugo.humami.service.MealQualityScoringService;
import org.springframework.stereotype.Service;

@Service
public class MealQualityBackfillService {

    private final MealRepository mealRepository;
    private final MealQualityScoringService mealQualityScoringService;

    public MealQualityBackfillService(MealRepository mealRepository, MealQualityScoringService mealQualityScoringService) {
        this.mealRepository = mealRepository;
        this.mealQualityScoringService = mealQualityScoringService;
    }

    public long backfillAll() {
        long updated = 0;
        for (MealEntity meal : mealRepository.findAll()) {
            meal.setQuality(mealQualityScoringService.score(meal));
            mealRepository.save(meal);
            updated++;
        }
        return updated;
    }
}
