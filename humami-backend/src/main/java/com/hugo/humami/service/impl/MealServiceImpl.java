package com.hugo.humami.service.impl;

import com.hugo.humami.domain.Ingredient;
import com.hugo.humami.domain.InstructionStep;
import com.hugo.humami.domain.MealEntity;
import com.hugo.humami.domain.Recipe;
import com.hugo.humami.dto.request.MealRequest;
import com.hugo.humami.dto.response.AutocompleteResponse;
import com.hugo.humami.dto.response.IngredientResponse;
import com.hugo.humami.dto.response.MealResponse;
import com.hugo.humami.dto.response.PagedResponse;
import com.hugo.humami.dto.response.RecipeIngredientsGroupResponse;
import com.hugo.humami.mapper.MealMapper;
import com.hugo.humami.repository.MealRepository;
import com.hugo.humami.service.EmbeddingService;
import com.hugo.humami.service.MealService;
import com.hugo.humami.service.S3Service;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class MealServiceImpl implements MealService {

    private static final int SEARCH_RESULTS_LIMIT = 20;

    private final MealRepository mealRepository;
    private final MealMapper mealMapper;
    private final EmbeddingService embeddingService;
    private final S3Service s3Service;

    public MealServiceImpl(MealRepository mealRepository, MealMapper mealMapper, EmbeddingService embeddingService, S3Service s3Service) {
        this.mealRepository = mealRepository;
        this.mealMapper = mealMapper;
        this.embeddingService = embeddingService;
        this.s3Service = s3Service;
    }

    @Override
    public List<MealResponse> getAll() {
        return mealRepository.findAll().stream()
                .map(this::toResponseWithImageIfAvailable)
                .collect(Collectors.toList());
    }

    @Override
    public PagedResponse<MealResponse> getPaged(String query, int page, int limit) {
        int normalizedPage = Math.max(page, 1);
        int normalizedLimit = Math.min(Math.max(limit, 1), 48);

        if (query == null || query.isBlank()) {
            Pageable pageable = PageRequest.of(normalizedPage - 1, normalizedLimit);
            Page<MealEntity> pageResult = mealRepository.findAll(pageable);
            List<MealResponse> items = pageResult.getContent().stream()
                    .map(this::toResponseWithImageIfAvailable)
                    .toList();

            return new PagedResponse<>(
                    items,
                    normalizedPage,
                    normalizedLimit,
                    pageResult.getTotalElements(),
                    pageResult.getTotalPages()
            );
        }

        String normalizedQuery = normalizeText(query);
        List<ScoredMeal> scored = mealRepository.findAll().stream()
                .map(meal -> new ScoredMeal(meal, fuzzyScore(meal, normalizedQuery)))
                .filter(scoredMeal -> scoredMeal.score() > 0)
                .sorted(Comparator.comparingInt(ScoredMeal::score).reversed())
                .toList();

        int totalItems = scored.size();
        int totalPages = Math.max((int) Math.ceil((double) totalItems / normalizedLimit), 1);
        int from = Math.min((normalizedPage - 1) * normalizedLimit, totalItems);
        int to = Math.min(from + normalizedLimit, totalItems);

        List<MealResponse> items = scored.subList(from, to).stream()
                .map(scoredMeal -> toResponseWithImageIfAvailable(scoredMeal.meal()))
                .toList();

        return new PagedResponse<>(items, normalizedPage, normalizedLimit, totalItems, totalPages);
    }

    @Override
    public MealResponse getById(String id) throws ChangeSetPersister.NotFoundException, IOException {
        MealEntity mealEntity = mealRepository.findById(id)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);
        MealResponse response = mealMapper.toResponse(mealEntity);
        response.setIngredientsByRecipe(buildIngredientsByRecipe(mealEntity));

        if (mealEntity.hasImage()){
            response.setImage(getImageUrl(mealEntity.getImage()));
        }

        return response;
    }

    @Override
    public MealResponse update(String id, MealRequest mealRequest){
        MealEntity existing = mealRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meal not found"));

        mealMapper.updateMealFromRequest(existing, mealRequest);
        normalizeRecipeStructure(existing);

        String textForEmbedding = getStringForEmbedding(existing);
        existing.setEmbedding(embeddingService.getEmbedding(textForEmbedding));

        MealEntity saved = mealRepository.save(existing);
        return mealMapper.toResponse(saved);
    }

    @Override
    public void setImage(String id, MultipartFile image) throws IOException {
        MealEntity existing = mealRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meal not found"));
        if (image != null && !image.isEmpty()) {
            String imageKey = s3Service.uploadImage(image, existing.getName());
            existing.setImage(imageKey);
            mealRepository.save(existing);
        }
    }

    private static String getStringForEmbedding(MealEntity updated) {
        return updated.getName() + ". " + updated.getDescription() + ". " +
                updated.getRecipes().stream()
                        .map(r -> r.getIngredients().toString())
                        .collect(Collectors.joining(", "));
    }

    @Override
    public MealResponse create(MealRequest mealRequest) {
        MealEntity mealEntity = mealMapper.toEntity(mealRequest);
        normalizeRecipeStructure(mealEntity);

        String embeddingText = getStringForEmbedding(mealEntity);
        mealEntity.setEmbedding(embeddingService.getEmbedding(embeddingText));

        MealEntity saved = mealRepository.save(mealEntity);
        return mealMapper.toResponse(saved);
    }

    @Override
    public void delete(String id) {
        MealEntity existing = mealRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meal not found"));

        if (existing.hasImage()) {
            s3Service.deleteImage(existing.getImage());
        }

        mealRepository.deleteById(id);
    }

    @Override
    public List<MealResponse> search(String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }

        String normalizedQuery = normalizeText(query);

        List<MealEntity> meals;
        try {
            meals = mealRepository.searchBySemanticText(query);
        } catch (Exception ignored) {
            meals = List.of();
        }

        if (meals == null || meals.isEmpty()) {
            meals = mealRepository.findAll().stream()
                    .map(meal -> new ScoredMeal(meal, fuzzyScore(meal, normalizedQuery)))
                    .filter(scoredMeal -> scoredMeal.score() > 0)
                    .sorted(Comparator.comparingInt(ScoredMeal::score).reversed())
                    .limit(SEARCH_RESULTS_LIMIT)
                    .map(ScoredMeal::meal)
                    .toList();
        }

        return meals.stream()
                .map(this::toResponseWithImageIfAvailable)
                .collect(Collectors.toList());
    }

    @Override
    public AutocompleteResponse autocomplete(String query) {
        List<String> mealNames = mealRepository.autocompleteMealNames(query)
                .stream()
                .map(MealEntity::getName)
                .collect(Collectors.toList());

        List<String> recipeTitles = mealRepository.autocompleteRecipeNames(query)
                .stream()
                .flatMap(meal -> meal.getRecipes().stream().map(Recipe::getName))
                .distinct()
                .collect(Collectors.toList());
        return new AutocompleteResponse(mealNames, recipeTitles);
    }

    private String getImageUrl(String image) throws IOException {
        return s3Service.getTempUrl(image);
    }

    private MealResponse toResponseWithImageIfAvailable(MealEntity mealEntity) {
        MealResponse response = mealMapper.toResponse(mealEntity);
        if (mealEntity.hasImage()) {
            try {
                response.setImage(getImageUrl(mealEntity.getImage()));
            } catch (IOException ignored) {
                // keep response without image URL if signing fails
            }
        }
        return response;
    }

    private List<RecipeIngredientsGroupResponse> buildIngredientsByRecipe(MealEntity mealEntity) {
        if (mealEntity.getRecipes() == null) {
            return List.of();
        }

        return mealEntity.getRecipes().stream().map(recipe -> {
            RecipeIngredientsGroupResponse group = new RecipeIngredientsGroupResponse();
            group.setRecipeName(recipe.getName());
            group.setRecipeDescription(recipe.getDescription());

            List<IngredientResponse> ingredients = recipe.getIngredients() == null
                    ? List.of()
                    : recipe.getIngredients().stream().map(mealMapper::toResponse).toList();

            group.setIngredients(ingredients);
            return group;
        }).toList();
    }

    private void normalizeRecipeStructure(MealEntity mealEntity) {
        if (mealEntity.getRecipes() == null) {
            return;
        }

        for (Recipe recipe : mealEntity.getRecipes()) {
            if ((recipe.getInstructionSteps() == null || recipe.getInstructionSteps().isEmpty())
                    && recipe.getInstructions() != null
                    && !recipe.getInstructions().isEmpty()) {
                List<InstructionStep> steps = new java.util.ArrayList<>();
                int order = 1;
                for (String text : recipe.getInstructions()) {
                    InstructionStep step = new InstructionStep();
                    step.setOrder(order++);
                    step.setText(text);
                    steps.add(step);
                }
                recipe.setInstructionSteps(steps);
            }

            if (recipe.getIngredients() != null) {
                for (Ingredient ingredient : recipe.getIngredients()) {
                    if (ingredient != null) {
                        ingredient.setOptional(ingredient.isOptional());
                    }
                }
            }
        }
    }

    private int fuzzyScore(MealEntity meal, String normalizedQuery) {
        if (normalizedQuery == null || normalizedQuery.isBlank()) {
            return 0;
        }

        int score = 0;

        score += scoreText(meal.getName(), normalizedQuery, 120);
        score += scoreText(meal.getDescription(), normalizedQuery, 70);

        if (meal.getRecipes() != null) {
            for (Recipe recipe : meal.getRecipes()) {
                score += scoreText(recipe.getName(), normalizedQuery, 80);
                score += scoreText(recipe.getDescription(), normalizedQuery, 35);

                if (recipe.getIngredients() != null) {
                    for (Ingredient ingredient : recipe.getIngredients()) {
                        if (ingredient != null) {
                            score += scoreText(ingredient.getName(), normalizedQuery, 75);
                        }
                    }
                }
            }
        }

        return score;
    }

    private int scoreText(String rawText, String normalizedQuery, int boost) {
        if (rawText == null || rawText.isBlank()) {
            return 0;
        }

        String normalizedText = normalizeText(rawText);
        if (normalizedText.isBlank()) {
            return 0;
        }

        if (normalizedText.equals(normalizedQuery)) {
            return boost + 30;
        }

        if (normalizedText.contains(normalizedQuery)) {
            return boost + 15;
        }

        List<String> textTokens = tokenize(normalizedText);
        List<String> queryTokens = tokenize(normalizedQuery);

        if (textTokens.isEmpty() || queryTokens.isEmpty()) {
            return 0;
        }

        int tokenScore = 0;
        for (String qToken : queryTokens) {
            int bestForToken = 0;
            for (String tToken : textTokens) {
                int similarity = tokenSimilarity(qToken, tToken);
                if (similarity > bestForToken) {
                    bestForToken = similarity;
                }
            }
            tokenScore += bestForToken;
        }

        int normalizedTokenScore = tokenScore / queryTokens.size();
        if (normalizedTokenScore < 55) {
            return 0;
        }

        return Math.round(boost * (normalizedTokenScore / 100f));
    }

    private int tokenSimilarity(String a, String b) {
        if (Objects.equals(a, b)) {
            return 100;
        }

        if (a.contains(b) || b.contains(a)) {
            return 90;
        }

        int distance = levenshteinDistance(a, b);
        int maxLen = Math.max(a.length(), b.length());
        if (maxLen == 0) {
            return 0;
        }

        float ratio = 1f - ((float) distance / maxLen);
        return Math.max(0, Math.round(ratio * 100));
    }

    private int levenshteinDistance(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];

        for (int i = 0; i <= a.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= b.length(); j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost
                );
            }
        }

        return dp[a.length()][b.length()];
    }

    private List<String> tokenize(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        String[] split = text.split("\\s+");
        List<String> tokens = new ArrayList<>(split.length);
        for (String token : split) {
            if (!token.isBlank()) {
                tokens.add(token);
            }
        }
        return tokens;
    }

    private String normalizeText(String text) {
        if (text == null) {
            return "";
        }

        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();

        return normalized;
    }

    private record ScoredMeal(MealEntity meal, int score) {}
}
