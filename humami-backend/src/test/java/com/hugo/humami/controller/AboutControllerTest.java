package com.hugo.humami.controller;

import com.hugo.humami.dto.response.AboutResponse;
import com.hugo.humami.service.AboutService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AboutController.class)
class AboutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AboutService aboutService;

    @Test
    void shouldReturnAboutPayload() throws Exception {
        AboutResponse about = new AboutResponse(
                "La historia detrás de Humami",
                List.of("L1", "L2", "L3"),
                "https://cdn.example.com/hugo.jpg",
                Instant.parse("2026-03-19T00:00:00Z")
        );
        when(aboutService.getAbout()).thenReturn(about);

        mockMvc.perform(get("/api/about"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("La historia detrás de Humami"))
                .andExpect(jsonPath("$.story[0]").value("L1"))
                .andExpect(jsonPath("$.photoUrl").value("https://cdn.example.com/hugo.jpg"))
                .andExpect(jsonPath("$.updatedAt").value("2026-03-19T00:00:00Z"));

        verify(aboutService).getAbout();
    }
}
