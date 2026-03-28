package com.hugo.humami.service.impl;

import com.hugo.humami.domain.Ingredient;
import com.hugo.humami.domain.InstructionStep;
import com.hugo.humami.domain.MealEntity;
import com.hugo.humami.domain.MealQuality;
import com.hugo.humami.domain.MealQualityBreakdown;
import com.hugo.humami.domain.Recipe;
import com.hugo.humami.service.MealQualityScoringService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
public class MealQualityScoringServiceImpl implements MealQualityScoringService {

    private static final int QUALITY_VERSION = 1;
    private static final List<String> VAGUE_PHRASES = List.of(
            "al gusto",
            "un poco",
            "lo suficiente",
            "hasta que este",
            "hasta que esté",
            "cuando este",
            "cuando esté"
    );

    @Override
    public MealQuality score(MealEntity mealEntity) {
        double completeness = completenessScore(mealEntity);
        double specificity = specificityScore(mealEntity);
        double media = mealEntity != null && mealEntity.hasImage() ? 1.0 : 0.0;

        double finalScore = clamp(
                (0.55 * completeness) +
                (0.30 * specificity) +
                (0.15 * media)
        );

        MealQualityBreakdown breakdown = new MealQualityBreakdown();
        breakdown.setCompleteness(round(completeness));
        breakdown.setSpecificity(round(specificity));
        breakdown.setMedia(round(media));

        MealQuality quality = new MealQuality();
        quality.setScore(round(finalScore));
        quality.setVersion(QUALITY_VERSION);
        quality.setBreakdown(breakdown);
        quality.setFlags(buildFlags(mealEntity, completeness, specificity, media));
        return quality;
    }

    private double completenessScore(MealEntity mealEntity) {
        double score = 0.0;

        if (hasMinText(mealEntity == null ? null : mealEntity.getName(), 5)) score += 0.10;
        if (hasMinText(mealEntity == null ? null : mealEntity.getDescription(), 30)) score += 0.10;
        if (countIngredients(mealEntity) >= 3) score += 0.25;
        if (countInstructionSteps(mealEntity) >= 3) score += 0.25;
        if (hasTimingMetadata(mealEntity)) score += 0.15;
        if (mealEntity != null && mealEntity.getServings() != null && mealEntity.getServings() > 0) score += 0.15;

        return clamp(score);
    }

    private double specificityScore(MealEntity mealEntity) {
        int totalIngredients = 0;
        int ingredientsWithQuantity = 0;
        int ingredientsWithUnit = 0;
        int totalSteps = 0;
        int detailedSteps = 0;
        int vagueMatches = 0;

        if (mealEntity != null && mealEntity.getRecipes() != null) {
            for (Recipe recipe : mealEntity.getRecipes()) {
                if (recipe == null) continue;

                if (recipe.getIngredients() != null) {
                    for (Ingredient ingredient : recipe.getIngredients()) {
                        if (ingredient == null || isBlank(ingredient.getName())) continue;
                        totalIngredients++;
                        if (ingredient.getQuantity() != null) ingredientsWithQuantity++;
                        if (ingredient.getUnit() != null) ingredientsWithUnit++;
                    }
                }

                for (InstructionStep step : safeSteps(recipe)) {
                    if (step == null || isBlank(step.getText())) continue;
                    totalSteps++;
                    String normalized = normalize(step.getText());
                    if (normalized.length() >= 25) detailedSteps++;
                    vagueMatches += countVagueMatches(normalized);
                }
            }
        }

        double quantityRatio = totalIngredients == 0 ? 0.0 : (double) ingredientsWithQuantity / totalIngredients;
        double unitRatio = totalIngredients == 0 ? 0.0 : (double) ingredientsWithUnit / totalIngredients;
        double detailRatio = totalSteps == 0 ? 0.0 : (double) detailedSteps / totalSteps;
        double vaguePenalty = totalSteps == 0 ? 0.0 : Math.min((double) vagueMatches / totalSteps, 1.0);

        double score =
                (0.40 * quantityRatio) +
                (0.30 * unitRatio) +
                (0.30 * detailRatio) -
                (0.20 * vaguePenalty);

        return clamp(score);
    }

    private List<String> buildFlags(MealEntity mealEntity, double completeness, double specificity, double media) {
        List<String> flags = new ArrayList<>();

        if (!hasMinText(mealEntity == null ? null : mealEntity.getDescription(), 30)) flags.add("missing-description");
        if (countIngredients(mealEntity) < 3) flags.add("not-enough-ingredients");
        if (countInstructionSteps(mealEntity) < 3) flags.add("not-enough-steps");
        if (!hasTimingMetadata(mealEntity)) flags.add("missing-time-metadata");
        if (mealEntity == null || mealEntity.getServings() == null || mealEntity.getServings() <= 0) flags.add("missing-servings");
        if (ingredientQuantityRatio(mealEntity) < 0.6) flags.add("ingredients-missing-quantities");
        if (ingredientUnitRatio(mealEntity) < 0.6) flags.add("ingredients-missing-units");
        if (media < 1.0) flags.add("missing-image");
        if (specificity < 0.5) flags.add("low-specificity");
        if (completeness < 0.5) flags.add("low-completeness");

        return flags.stream().filter(Objects::nonNull).distinct().toList();
    }

    private double ingredientQuantityRatio(MealEntity mealEntity) {
        int total = 0;
        int withQuantity = 0;
        if (mealEntity != null && mealEntity.getRecipes() != null) {
            for (Recipe recipe : mealEntity.getRecipes()) {
                if (recipe == null || recipe.getIngredients() == null) continue;
                for (Ingredient ingredient : recipe.getIngredients()) {
                    if (ingredient == null || isBlank(ingredient.getName())) continue;
                    total++;
                    if (ingredient.getQuantity() != null) withQuantity++;
                }
            }
        }
        return total == 0 ? 0.0 : (double) withQuantity / total;
    }

    private double ingredientUnitRatio(MealEntity mealEntity) {
        int total = 0;
        int withUnit = 0;
        if (mealEntity != null && mealEntity.getRecipes() != null) {
            for (Recipe recipe : mealEntity.getRecipes()) {
                if (recipe == null || recipe.getIngredients() == null) continue;
                for (Ingredient ingredient : recipe.getIngredients()) {
                    if (ingredient == null || isBlank(ingredient.getName())) continue;
                    total++;
                    if (ingredient.getUnit() != null) withUnit++;
                }
            }
        }
        return total == 0 ? 0.0 : (double) withUnit / total;
    }

    private int countIngredients(MealEntity mealEntity) {
        if (mealEntity == null || mealEntity.getRecipes() == null) return 0;
        int total = 0;
        for (Recipe recipe : mealEntity.getRecipes()) {
            if (recipe == null || recipe.getIngredients() == null) continue;
            for (Ingredient ingredient : recipe.getIngredients()) {
                if (ingredient != null && !isBlank(ingredient.getName())) total++;
            }
        }
        return total;
    }

    private int countInstructionSteps(MealEntity mealEntity) {
        if (mealEntity == null || mealEntity.getRecipes() == null) return 0;
        int total = 0;
        for (Recipe recipe : mealEntity.getRecipes()) {
            total += safeSteps(recipe).stream().filter(step -> step != null && !isBlank(step.getText())).count();
        }
        return total;
    }

    private boolean hasTimingMetadata(MealEntity mealEntity) {
        return mealEntity != null
                && mealEntity.getTimings() != null
                && ((mealEntity.getTimings().getPrepTimeInHours() != null && mealEntity.getTimings().getPrepTimeInHours() > 0)
                || (mealEntity.getTimings().getTotalTimeInHours() != null && mealEntity.getTimings().getTotalTimeInHours() > 0));
    }

    private List<InstructionStep> safeSteps(Recipe recipe) {
        if (recipe == null || recipe.getInstructionSteps() == null) {
            return List.of();
        }
        return recipe.getInstructionSteps();
    }

    private boolean hasMinText(String value, int minLength) {
        return value != null && value.trim().length() >= minLength;
    }

    private int countVagueMatches(String normalizedText) {
        int matches = 0;
        for (String phrase : VAGUE_PHRASES) {
            if (normalizedText.contains(phrase)) matches++;
        }
        return matches;
    }

    private String normalize(String text) {
        return text == null ? "" : text.toLowerCase(Locale.ROOT).trim();
    }

    private boolean isBlank(String text) {
        return text == null || text.trim().isEmpty();
    }

    private double clamp(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
