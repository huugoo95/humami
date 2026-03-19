package com.hugo.humami.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hugo.humami.config.WriteAuthConfig;
import com.hugo.humami.config.WriteAuthInterceptor;
import com.hugo.humami.dto.request.AboutRequest;
import com.hugo.humami.dto.response.AboutResponse;
import com.hugo.humami.service.AboutService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AboutController.class)
@Import({WriteAuthConfig.class, WriteAuthInterceptor.class})
@TestPropertySource(properties = "humami.auth.write-secret=test-secret")
class AboutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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

    @Test
    void shouldRejectUpdateWithoutSecret() throws Exception {
        mockMvc.perform(patch("/api/about")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AboutRequest())))
                .andExpect(status().isUnauthorized());

        verify(aboutService, never()).updateAbout(any(AboutRequest.class));
    }

    @Test
    void shouldAllowUpdateWithSecret() throws Exception {
        AboutResponse updated = new AboutResponse(
                "Nuevo título",
                List.of("A", "B"),
                "https://cdn.example.com/new.jpg",
                Instant.parse("2026-03-19T21:30:00Z")
        );
        when(aboutService.updateAbout(any(AboutRequest.class))).thenReturn(updated);

        AboutRequest request = new AboutRequest("Nuevo título", List.of("A", "B"), "https://cdn.example.com/new.jpg");

        mockMvc.perform(patch("/api/about")
                        .header("X-HUMAMI-SECRET", "test-secret")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Nuevo título"))
                .andExpect(jsonPath("$.story[1]").value("B"));

        verify(aboutService).updateAbout(any(AboutRequest.class));
    }

    @Test
    void shouldRejectImageUploadWithoutSecret() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "photo.jpg", "image/jpeg", "abc".getBytes());

        mockMvc.perform(multipart("/api/about/image").file(image))
                .andExpect(status().isUnauthorized());

        verify(aboutService, never()).updateAboutImage(any());
    }

    @Test
    void shouldAllowImageUploadWithSecret() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "photo.jpg", "image/jpeg", "abc".getBytes());

        AboutResponse updated = new AboutResponse(
                "Título",
                List.of("A"),
                "https://signed.example.com/photo.jpg",
                Instant.parse("2026-03-19T22:00:00Z")
        );
        when(aboutService.updateAboutImage(any())).thenReturn(updated);

        mockMvc.perform(multipart("/api/about/image")
                        .file(image)
                        .header("X-HUMAMI-SECRET", "test-secret")
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.photoUrl").value("https://signed.example.com/photo.jpg"));

        verify(aboutService).updateAboutImage(any());
    }
}
