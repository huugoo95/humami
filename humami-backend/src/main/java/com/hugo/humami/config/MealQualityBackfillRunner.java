package com.hugo.humami.config;

import com.hugo.humami.service.impl.MealQualityBackfillService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "humami.meals.quality.backfill.enabled", havingValue = "true")
public class MealQualityBackfillRunner implements CommandLineRunner {

    private final MealQualityBackfillService mealQualityBackfillService;

    public MealQualityBackfillRunner(MealQualityBackfillService mealQualityBackfillService) {
        this.mealQualityBackfillService = mealQualityBackfillService;
    }

    @Override
    public void run(String... args) {
        long updated = mealQualityBackfillService.backfillAll();
        System.out.println("[meal-quality-backfill] Updated meals: " + updated);
    }
}
