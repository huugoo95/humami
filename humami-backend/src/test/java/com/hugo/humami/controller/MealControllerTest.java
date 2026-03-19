package com.hugo.humami.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hugo.humami.config.WriteAuthConfig;
import com.hugo.humami.config.WriteAuthInterceptor;
import com.hugo.humami.dto.request.MealRequest;
import com.hugo.humami.dto.response.AutocompleteResponse;
import com.hugo.humami.dto.response.MealResponse;
import com.hugo.humami.dto.response.PagedResponse;
import com.hugo.humami.service.MealService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MealController.class)
@Import({WriteAuthConfig.class, WriteAuthInterceptor.class})
@TestPropertySource(properties = "humami.auth.write-secret=test-secret")
class MealControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MealService mealService;

    @Test
    void shouldGetPagedMealsWithDefaultParams() throws Exception {
        PagedResponse<MealResponse> paged = new PagedResponse<>(List.of(new MealResponse()), 1, 12, 1, 1);
        when(mealService.getPaged("", 1, 12)).thenReturn(paged);

        mockMvc.perform(get("/api/meals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.limit").value(12))
                .andExpect(jsonPath("$.totalItems").value(1));

        verify(mealService).getPaged("", 1, 12);
    }

    @Test
    void shouldGetPagedMealsWithCustomParams() throws Exception {
        PagedResponse<MealResponse> paged = new PagedResponse<>(List.of(), 2, 5, 0, 0);
        when(mealService.getPaged("pasta", 2, 5)).thenReturn(paged);

        mockMvc.perform(get("/api/meals").param("query", "pasta").param("page", "2").param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(2))
                .andExpect(jsonPath("$.limit").value(5));

        verify(mealService).getPaged("pasta", 2, 5);
    }

    @Test
    void shouldSearchMeals() throws Exception {
        MealResponse meal = new MealResponse();
        meal.setName("Paella");
        when(mealService.search("pae")).thenReturn(List.of(meal));

        mockMvc.perform(get("/api/meals/search").param("query", "pae"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Paella"));

        verify(mealService).search("pae");
    }

    @Test
    void shouldGetMealById() throws Exception {
        MealResponse meal = new MealResponse();
        meal.setId("meal-1");
        meal.setName("Tortilla");
        when(mealService.getById("meal-1")).thenReturn(meal);

        mockMvc.perform(get("/api/meals/meal-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("meal-1"))
                .andExpect(jsonPath("$.name").value("Tortilla"));

        verify(mealService).getById("meal-1");
    }

    @Test
    void shouldRejectCreateWithoutSecretHeader() throws Exception {
        mockMvc.perform(post("/api/meals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new MealRequest())))
                .andExpect(status().isUnauthorized());

        verify(mealService, never()).create(any(MealRequest.class));
    }

    @Test
    void shouldCreateMealWithValidSecretHeader() throws Exception {
        MealResponse created = new MealResponse();
        created.setId("new-id");
        when(mealService.create(any(MealRequest.class))).thenReturn(created);

        mockMvc.perform(post("/api/meals")
                        .header("X-HUMAMI-SECRET", "test-secret")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new MealRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("new-id"));

        verify(mealService).create(any(MealRequest.class));
    }

    @Test
    void shouldUpdateMealWithValidSecretHeader() throws Exception {
        MealResponse updated = new MealResponse();
        updated.setName("Updated");
        when(mealService.update(anyString(), any(MealRequest.class))).thenReturn(updated);

        mockMvc.perform(patch("/api/meals/meal-1")
                        .header("X-HUMAMI-SECRET", "test-secret")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new MealRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));

        verify(mealService).update(anyString(), any(MealRequest.class));
    }

    @Test
    void shouldRejectUpdateWithoutSecretHeader() throws Exception {
        mockMvc.perform(patch("/api/meals/meal-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new MealRequest())))
                .andExpect(status().isUnauthorized());

        verify(mealService, never()).update(anyString(), any(MealRequest.class));
    }

    @Test
    void shouldSetMealImageWithValidSecretHeader() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "meal.jpg", "image/jpeg", "image-data".getBytes());

        mockMvc.perform(multipart("/api/meals/{id}/image", "meal-1")
                        .file(image)
                        .header("X-HUMAMI-SECRET", "test-secret")
                        .with(req -> {
                            req.setMethod("PUT");
                            return req;
                        }))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(mealService).setImage(anyString(), any(org.springframework.web.multipart.MultipartFile.class));
    }

    @Test
    void shouldDeleteMealWithValidSecretHeader() throws Exception {
        mockMvc.perform(delete("/api/meals/meal-1")
                        .header("X-HUMAMI-SECRET", "test-secret"))
                .andExpect(status().isNoContent());

        verify(mealService).delete("meal-1");
    }

    @Test
    void shouldAutocompleteMeals() throws Exception {
        when(mealService.autocomplete("tor")).thenReturn(new AutocompleteResponse(List.of("Tortilla"), List.of("Base")));

        mockMvc.perform(get("/api/meals/autocomplete").param("query", "tor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mealNames[0]").value("Tortilla"))
                .andExpect(jsonPath("$.recipeTitles[0]").value("Base"));

        verify(mealService).autocomplete("tor");
    }
}
