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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MealServiceImpl implements MealService {

    private final MealRepository mealRepository;
    private final MealMapper mealMapper;
    private final EmbeddingService embeddingService;

    public MealServiceImpl(MealRepository mealRepository, MealMapper mealMapper, EmbeddingService embeddingService) {
        this.mealRepository = mealRepository;
        this.mealMapper = mealMapper;
        this.embeddingService = embeddingService;
    }

    @Override
    public List<MealResponse> getAll() {
        return mealRepository.findAll().stream()
                .map(mealMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MealResponse getById(String id) {
        Optional<MealEntity> mealEntity = mealRepository.findById(id);
        return mealEntity.map(mealMapper::toResponse).orElse(null);
    }

    @Override
    public MealResponse save(MealRequest mealRequest) {
        MealEntity mealEntity = mealMapper.toEntity(mealRequest);
        //ToDo add embedding
        MealEntity savedMeal = mealRepository.save(mealEntity);
        return mealMapper.toResponse(savedMeal);
    }

    @Override
    public void delete(String id) {
        mealRepository.deleteById(id);
    }

    @Override
    public List<MealResponse> search(String query) {
        List<Double> queryVector = embeddingService.generateEmbedding(query);
        List<MealEntity> meals = mealRepository.searchByEmbedding(queryVector);
        return meals.stream()
                .map(mealMapper::toResponse)
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
}