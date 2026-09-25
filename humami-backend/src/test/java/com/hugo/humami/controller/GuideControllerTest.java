package com.hugo.humami.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hugo.humami.config.WriteAuthConfig;
import com.hugo.humami.config.WriteAuthInterceptor;
import com.hugo.humami.dto.request.GuideAccessRequest;
import com.hugo.humami.dto.request.GuideRequest;
import com.hugo.humami.dto.response.GuideAccessResponse;
import com.hugo.humami.dto.response.GuideResponse;
import com.hugo.humami.service.GuideService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GuideController.class)
@Import({WriteAuthConfig.class, WriteAuthInterceptor.class})
@TestPropertySource(properties = "humami.auth.write-secret=test-secret")
class GuideControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private GuideService guideService;

    @Test
    void shouldListPublishedGuidesWithoutWriteSecret() throws Exception {
        GuideResponse guide = new GuideResponse();
        guide.setSlug("pizza-napolitana-desde-cero");
        when(guideService.listPublished()).thenReturn(List.of(guide));

        mockMvc.perform(get("/api/guides"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].slug").value("pizza-napolitana-desde-cero"));
    }

    @Test
    void shouldRejectEditorCreateWithoutWriteSecret() throws Exception {
        mockMvc.perform(post("/api/guides").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(new GuideRequest())))
                .andExpect(status().isUnauthorized());
        verify(guideService, never()).create(any());
    }

    @Test
    void shouldAcceptPublicAccessRequestAndSetHttpOnlyCookie() throws Exception {
        when(guideService.requestAccess(eq("pizza-napolitana-desde-cero"), any(), any())).thenReturn("opaque-token");
        GuideAccessRequest request = new GuideAccessRequest();
        request.setEmail("reader@example.com");
        request.setPrivacyNoticeVersion("2026-09");
        request.setPrivacyAccepted(true);

        mockMvc.perform(post("/api/guides/pizza-napolitana-desde-cero/access")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.readerUrl").value("/guias/pizza-napolitana-desde-cero/leer"))
                .andExpect(cookie().httpOnly("humami_guide_pizza-napolitana-desde-cero", true));
        verify(guideService).requestAccess(eq("pizza-napolitana-desde-cero"), any(), any());
    }
}
