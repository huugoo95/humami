package com.hugo.humami.repository;

import com.hugo.humami.domain.MealEntity;
import com.hugo.humami.domain.MealQuality;
import com.hugo.humami.dto.response.MealResponse;
import com.hugo.humami.mapper.MealMapper;
import com.hugo.humami.service.*;
import com.hugo.humami.service.impl.MealServiceImpl;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.Document;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;

import java.util.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/** Run explicitly with -Dhumami.test.mongoUri=mongodb://127.0.0.1:<isolated-port>. */
@EnabledIfSystemProperty(named = "humami.test.mongoUri", matches = ".+")
class MealPaginationIntegrationTest {
    private MongoClient client;
    private MongoTemplate template;
    private MealRepository repository;
    private MealServiceImpl service;

    @BeforeEach
    void setup() {
        client = MongoClients.create(System.getProperty("humami.test.mongoUri"));
        template = new MongoTemplate(client, "humami_test_017_" + UUID.randomUUID().toString().replace("-", ""));
        repository = new MongoRepositoryFactory(template).getRepository(MealRepository.class);
        MealMapper mapper = mock(MealMapper.class);
        when(mapper.toResponse(any(MealEntity.class))).thenAnswer(invocation -> {
            MealResponse response = new MealResponse();
            response.setId(((MealEntity) invocation.getArgument(0)).getId());
            return response;
        });
        service = new MealServiceImpl(repository, mapper, mock(EmbeddingService.class), mock(S3Service.class), mock(MealQualityScoringService.class));
    }

    @AfterEach
    void cleanup() {
        if (template != null) template.getDb().drop();
        if (client != null) client.close();
    }

    private MealEntity meal(String id, Double score) {
        MealEntity meal = new MealEntity();
        meal.setId(id);
        meal.setName("Arroz");
        if (score != null) {
            MealQuality quality = new MealQuality();
            quality.setScore(score);
            meal.setQuality(quality);
        }
        return meal;
    }

    @Test
    void filtersBeforePagingAndCountsOnlyEligibleRecords() {
        for (int i = 39; i >= 0; i--) repository.save(meal(String.format("meal-%02d", i), i < 25 ? 0.5 : 0.49));
        List<String> ids = new ArrayList<>();
        for (int page = 1; page <= 3; page++) {
            var result = service.getPaged(" ", page, 12);
            assertEquals(page == 3 ? 1 : 12, result.getItems().size());
            assertEquals(25, result.getTotalItems());
            assertEquals(3, result.getTotalPages());
            ids.addAll(result.getItems().stream().map(MealResponse::getId).toList());
        }
        assertEquals(IntStream.range(0, 25).mapToObj(i -> String.format("meal-%02d", i)).toList(), ids);
        assertEquals(ids.subList(0, 12), service.getPaged("", 1, 12).getItems().stream().map(MealResponse::getId).toList());
        var beyond = service.getPaged("", 4, 12);
        assertTrue(beyond.getItems().isEmpty());
        assertEquals(4, beyond.getPage());
        assertEquals(25, beyond.getTotalItems());
        assertEquals(3, beyond.getTotalPages());
    }

    @Test
    void preservesLegacyEffectiveZeroAndThresholdEquality() {
        repository.save(meal("boundary", 0.5));
        repository.save(meal("low", 0.49));
        repository.save(meal("absent", null));
        template.getCollection("meals").insertMany(List.of(
                new Document("_id", "null-quality").append("quality", null),
                new Document("_id", "null-score").append("quality", new Document("score", null)),
                new Document("_id", "missing-score").append("quality", new Document())));
        assertEquals(List.of("boundary"), service.getPaged("", 1, 12).getItems().stream().map(MealResponse::getId).toList());
        assertEquals(6, service.getPaged("", 1, 12, 0).getTotalItems());
        assertEquals(6, service.getPaged("", 1, 12, -0.1).getTotalItems());
    }

    @Test
    void handlesEmptyResultsAndNormalization() {
        var empty = service.getPaged("", -1, 0);
        assertTrue(empty.getItems().isEmpty());
        assertEquals(0, empty.getTotalItems());
        assertEquals(1, empty.getTotalPages());
        assertEquals(1, empty.getPage());
        assertEquals(1, empty.getLimit());
        assertEquals(48, service.getPaged("", 1, 100).getLimit());
    }

    @Test
    void searchTiesFollowDatabaseIdOrderAcrossPages() {
        // Spring converts valid hexadecimal String IDs to BSON ObjectId; other IDs stay strings.
        List<String> ids = List.of("ffffffffffffffffffffffff", "000000000000000000000001", "z", "a");
        ids.forEach(id -> repository.save(meal(id, 0.8)));
        repository.save(meal("hidden", 0.1));
        List<String> expected = List.of("a", "z", "000000000000000000000001", "ffffffffffffffffffffffff");
        List<String> actual = new ArrayList<>();
        for (int page = 1; page <= 2; page++) {
            var result = service.getPaged("arroz", page, 2);
            assertEquals(4, result.getTotalItems());
            assertEquals(2, result.getTotalPages());
            actual.addAll(result.getItems().stream().map(MealResponse::getId).toList());
        }
        assertEquals(expected, actual);
        assertEquals(expected, service.getPaged("", 1, 12).getItems().stream().map(MealResponse::getId).toList());
        var emptySearch = service.getPaged("nonmatchingword", 1, 12);
        assertEquals(0, emptySearch.getTotalItems());
        assertEquals(1, emptySearch.getTotalPages());
        var beyond = service.getPaged("arroz", Integer.MAX_VALUE, 48);
        assertTrue(beyond.getItems().isEmpty());
        assertEquals(4, beyond.getTotalItems());
        assertEquals(Integer.MAX_VALUE, beyond.getPage());
    }
}
