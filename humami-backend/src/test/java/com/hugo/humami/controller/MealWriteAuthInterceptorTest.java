package com.hugo.humami.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hugo.humami.config.WriteAuthConfig;
import com.hugo.humami.config.WriteAuthInterceptor;
import com.hugo.humami.dto.request.MealRequest;
import com.hugo.humami.dto.response.MealResponse;
import com.hugo.humami.service.MealService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MealController.class)
@Import({WriteAuthConfig.class, WriteAuthInterceptor.class})
@TestPropertySource(properties = "humami.auth.write-secret=test-secret")
class MealWriteAuthInterceptorTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MealService mealService;

    @Test
    void shouldRejectCreateWithoutSecretHeader() throws Exception {
        mockMvc.perform(post("/api/meals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new MealRequest())))
                .andExpect(status().isUnauthorized());

        verify(mealService, never()).create(any(MealRequest.class));
    }

    @Test
    void shouldAllowCreateWithValidSecretHeader() throws Exception {
        when(mealService.create(any(MealRequest.class))).thenReturn(new MealResponse());

        mockMvc.perform(post("/api/meals")
                        .header("X-HUMAMI-SECRET", "test-secret")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new MealRequest())))
                .andExpect(status().isCreated());

        verify(mealService).create(any(MealRequest.class));
    }

    @Test
    void shouldRejectUpdateWithoutSecretHeader() throws Exception {
        mockMvc.perform(patch("/api/meals/meal-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new MealRequest())))
                .andExpect(status().isUnauthorized());

        verify(mealService, never()).update(anyString(), any(MealRequest.class));
    }
}
