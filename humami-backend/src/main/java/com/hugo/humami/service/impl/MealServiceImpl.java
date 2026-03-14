package com.hugo.humami.service.impl;

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
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MealServiceImpl implements MealService {

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
        Pageable pageable = PageRequest.of(normalizedPage - 1, normalizedLimit);

        Page<MealEntity> pageResult = (query == null || query.isBlank())
                ? mealRepository.findAll(pageable)
                : mealRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query, pageable);

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
        String text = updated.getName() + ". " + updated.getDescription() + ". " +
                updated.getRecipes().stream()
                        .map(r -> r.getIngredients().toString())
                        .collect(Collectors.joining(", "));
        return text;
    }

    @Override
    public MealResponse create(MealRequest mealRequest) {
        MealEntity mealEntity = mealMapper.toEntity(mealRequest);

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
        List<Double> queryVector = null;
        // ToDo get embedding
        //  embeddingService.getEmbedding(query);
        List<MealEntity> meals = isValidEmbedding(queryVector)
                ? mealRepository.searchByEmbedding(queryVector)
                : fallbackSearch(query);
        return meals.stream()
                .map(this::toResponseWithImageIfAvailable)
                .collect(Collectors.toList());
    }

    private boolean isValidEmbedding(List<Double> embedding) {
        return embedding != null && !embedding.isEmpty();
    }

    private List<MealEntity> fallbackSearch(String query) {
        return mealRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query);
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
}