package com.hugo.humami.service.impl;

import com.hugo.humami.domain.MealEntity;
import com.hugo.humami.domain.Recipe;
import com.hugo.humami.dto.request.MealRequest;
import com.hugo.humami.dto.response.AutocompleteResponse;
import com.hugo.humami.dto.response.MealResponse;
import com.hugo.humami.mapper.MealMapper;
import com.hugo.humami.repository.MealRepository;
import com.hugo.humami.service.EmbeddingService;
import com.hugo.humami.service.MealService;
import com.hugo.humami.service.S3Service;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

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
                .map(mealMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MealResponse getById(String id) throws ChangeSetPersister.NotFoundException, IOException {
        MealEntity mealEntity = mealRepository.findById(id)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);
        MealResponse response = mealMapper.toResponse(mealEntity);
        String imageUrl = s3Service.getTempUrl(mealEntity.getImage());
        response.setImage(imageUrl);

        return response;
    }

    @Override
    public MealResponse save(MealRequest mealRequest) {
        MealEntity mealEntity = mealMapper.toEntity(mealRequest);
        String ingredientsText = mealEntity.getRecipes().stream()
                .map(recipe -> recipe.getIngredients() + ".")
                .collect(Collectors.joining(", "));
        String text = String.format("%s. %s. Ingredients: %s",
                mealEntity.getName(),
                mealEntity.getDescription(), ingredientsText);
        mealEntity.setEmbedding(embeddingService.getEmbedding(text));
        MealEntity savedMeal = mealRepository.save(mealEntity);
        return mealMapper.toResponse(savedMeal);
    }

    @Override
    public void delete(String id) {
        mealRepository.deleteById(id);
    }

    @Override
    public List<MealResponse> search(String query) {
        List<Double> queryVector = embeddingService.getEmbedding(query);
        List<MealEntity> meals = isValidEmbedding(queryVector)
                ? mealRepository.searchByEmbedding(queryVector)
                : fallbackSearch(query);
        return meals.stream()
                .map(mealMapper::toResponse)
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
}