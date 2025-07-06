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
    public MealResponse update(String id, MealRequest mealRequest, MultipartFile image) throws IOException {
        MealEntity existing = mealRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meal not found"));

        mealMapper.updateMealFromRequest(existing, mealRequest);

        if (image != null && !image.isEmpty()) {
            String imageKey = s3Service.uploadImage(image);
            existing.setImage(imageKey);
        }

        String textForEmbedding = getStringForEmbedding(existing);
        existing.setEmbedding(embeddingService.getEmbedding(textForEmbedding));

        MealEntity saved = mealRepository.save(existing);
        return mealMapper.toResponse(saved);
    }

    private static String getStringForEmbedding(MealEntity updated) {
        String text = updated.getName() + ". " + updated.getDescription() + ". " +
                updated.getRecipes().stream()
                        .map(r -> r.getIngredients().toString())
                        .collect(Collectors.joining(", "));
        return text;
    }

    @Override
    public MealResponse create(MealRequest mealRequest, MultipartFile image) throws IOException {
        MealEntity mealEntity = mealMapper.toEntity(mealRequest);

        if(image != null && !image.isEmpty()){
            String imageKey = s3Service.uploadImage(image);
            mealEntity.setImage(imageKey);
        }

        String embeddingText = getStringForEmbedding(mealEntity);
        mealEntity.setEmbedding(embeddingService.getEmbedding(embeddingText));

        MealEntity saved = mealRepository.save(mealEntity);
        return mealMapper.toResponse(saved);
    }

    @Override
    public void delete(String id) {
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