package com.hugo.humami.controller;

import com.hugo.humami.config.WriteAuthConfig;
import com.hugo.humami.config.WriteAuthInterceptor;
import com.hugo.humami.service.BlogPostService;
import com.hugo.humami.service.MealService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({MealController.class, BlogPostController.class})
@Import({WriteAuthConfig.class, WriteAuthInterceptor.class})
@TestPropertySource(properties = "humami.auth.write-secret=")
class WriteAuthInterceptorConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MealService mealService;

    @MockBean
    private BlogPostService blogPostService;

    @Test
    void shouldReturnServiceUnavailableWhenSecretNotConfiguredForMealWrites() throws Exception {
        mockMvc.perform(post("/api/meals"))
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    void shouldReturnServiceUnavailableWhenSecretNotConfiguredForBlogWrites() throws Exception {
        mockMvc.perform(post("/api/blog"))
                .andExpect(status().isServiceUnavailable());
    }
}
