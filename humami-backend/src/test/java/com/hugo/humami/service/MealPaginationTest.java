package com.hugo.humami.service;

import com.hugo.humami.domain.MealEntity;
import com.hugo.humami.domain.MealQuality;
import com.hugo.humami.dto.response.MealResponse;
import com.hugo.humami.mapper.MealMapper;
import com.hugo.humami.repository.MealRepository;
import com.hugo.humami.service.impl.MealServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MealPaginationTest {
    private final MealRepository repository = mock(MealRepository.class);
    private final MealMapper mapper = mock(MealMapper.class);
    private final MealServiceImpl service = new MealServiceImpl(repository, mapper,
            mock(EmbeddingService.class), mock(S3Service.class), mock(MealQualityScoringService.class));

    @Test
    void returnsFullEligibleCountRatherThanPageSize() {
        var pageable = qualityOrderedPage(1, 12);
        MealEntity meal = new MealEntity();
        when(repository.findEligible(0.5, pageable)).thenReturn(new PageImpl<>(List.of(meal), pageable, 25));
        when(mapper.toResponse(meal)).thenReturn(new MealResponse());
        var result = service.getPaged("", 2, 12);
        assertEquals(25, result.getTotalItems());
        assertEquals(3, result.getTotalPages());
        assertEquals(1, result.getItems().size());
        verify(repository, never()).findAll();
    }

    @Test
    void zeroThresholdUsesLegacyAwareQueryAndNormalizesPage() {
        var pageable = qualityOrderedPage(0, 48);
        when(repository.findEligibleIncludingUnscored(0, pageable)).thenReturn(new PageImpl<>(List.of(), pageable, 0));
        var result = service.getPaged(null, -1, 100, 0);
        assertEquals(1, result.getPage());
        assertEquals(48, result.getLimit());
        assertEquals(0, result.getTotalItems());
        assertEquals(1, result.getTotalPages());
    }

    @Test
    void blankCatalogueOrdersByQualityDescendingThenIdAscending() {
        var pageable = PageRequest.of(0, 12,
                Sort.by("quality.score").descending().and(Sort.by("id").ascending()));
        MealEntity carbonara = meal("z", "Carbonara");
        carbonara.getQuality().setScore(0.97);
        MealEntity lowerQuality = meal("a", "Arroz");
        lowerQuality.getQuality().setScore(0.80);
        when(repository.findEligible(0.5, pageable))
                .thenReturn(new PageImpl<>(List.of(carbonara, lowerQuality), pageable, 2));
        when(mapper.toResponse(any(MealEntity.class))).thenAnswer(invocation -> {
            MealResponse response = new MealResponse();
            response.setId(((MealEntity) invocation.getArgument(0)).getId());
            return response;
        });

        var result = service.getPaged(" ", 1, 12);

        assertEquals(List.of("z", "a"), result.getItems().stream().map(MealResponse::getId).toList());
    }

    @Test
    void searchPreservesRelevanceBeforeIdAndHandlesExtremePage() {
        MealEntity lowerRelevance = meal("a", "Arroz integral");
        MealEntity exact = meal("z", "Arroz");
        when(repository.findAll()).thenReturn(List.of(lowerRelevance, exact));
        when(mapper.toResponse(any(MealEntity.class))).thenAnswer(invocation -> {
            MealResponse response = new MealResponse();
            response.setId(((MealEntity) invocation.getArgument(0)).getId());
            return response;
        });
        assertEquals("z", service.getPaged("arroz", 1, 1).getItems().get(0).getId());
        assertEquals("a", service.getPaged("arroz", 2, 1).getItems().get(0).getId());
        var beyond = service.getPaged("arroz", Integer.MAX_VALUE, 48);
        assertTrue(beyond.getItems().isEmpty());
        assertEquals(2, beyond.getTotalItems());
    }

    private MealEntity meal(String id, String name) {
        MealEntity meal = new MealEntity();
        meal.setId(id);
        meal.setName(name);
        MealQuality quality = new MealQuality();
        quality.setScore(0.8);
        meal.setQuality(quality);
        return meal;
    }

    private PageRequest qualityOrderedPage(int page, int limit) {
        return PageRequest.of(page, limit,
                Sort.by("quality.score").descending().and(Sort.by("id").ascending()));
    }
}
