package com.hugo.humami.service.impl;

import com.hugo.humami.domain.guides.GuideAccessTokenEntity;
import com.hugo.humami.domain.guides.GuideEntity;
import com.hugo.humami.dto.request.GuideAccessRequest;
import com.hugo.humami.mapper.GuideMapper;
import com.hugo.humami.repository.GuideAccessTokenRepository;
import com.hugo.humami.repository.GuideLeadRepository;
import com.hugo.humami.repository.GuideRepository;
import com.hugo.humami.service.GuideAccessEmailSender;
import com.hugo.humami.service.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GuideServiceImplTest {
    @Mock private GuideRepository guideRepository;
    @Mock private GuideLeadRepository guideLeadRepository;
    @Mock private GuideAccessTokenRepository tokenRepository;
    @Mock private GuideMapper guideMapper;
    @Mock private S3Service s3Service;
    @Mock private GuideAccessRateLimiter rateLimiter;
    @Mock private GuideAccessEmailSender emailSender;
    private GuideServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new GuideServiceImpl(guideRepository, guideLeadRepository, tokenRepository, guideMapper, s3Service,
                rateLimiter, emailSender, "https://humami.es/api", 30);
    }

    @Test
    void rejectsAccessWithoutExplicitPrivacyAcceptance() {
        when(guideRepository.findBySlugAndStatus("pizza", "published")).thenReturn(Optional.of(guide("guide-a")));
        GuideAccessRequest request = new GuideAccessRequest();
        request.setEmail("reader@example.com");
        request.setPrivacyNoticeVersion("2026-09");

        ResponseStatusException error = assertThrows(ResponseStatusException.class, () -> service.requestAccess("pizza", request, "203.0.113.1"));

        assertEquals(400, error.getStatusCode().value());
        verifyNoInteractions(rateLimiter, guideLeadRepository, tokenRepository, emailSender);
    }

    @Test
    void rejectsTokenCreatedForAnotherGuide() throws Exception {
        when(guideRepository.findBySlugAndStatus("pizza", "published")).thenReturn(Optional.of(guide("guide-a")));
        GuideAccessTokenEntity token = token("guide-b", Instant.now().plusSeconds(60));
        when(tokenRepository.findByTokenHash(anyString())).thenReturn(Optional.of(token));

        ResponseStatusException error = assertThrows(ResponseStatusException.class, () -> service.getDocument("pizza", "opaque"));

        assertEquals(401, error.getStatusCode().value());
        verifyNoInteractions(s3Service);
    }

    @Test
    void rejectsExpiredToken() throws Exception {
        when(guideRepository.findBySlugAndStatus("pizza", "published")).thenReturn(Optional.of(guide("guide-a")));
        when(tokenRepository.findByTokenHash(anyString())).thenReturn(Optional.of(token("guide-a", Instant.now().minusSeconds(1))));

        ResponseStatusException error = assertThrows(ResponseStatusException.class, () -> service.getDocument("pizza", "opaque"));

        assertEquals(401, error.getStatusCode().value());
        verifyNoInteractions(s3Service);
    }

    private static GuideEntity guide(String id) {
        GuideEntity guide = new GuideEntity();
        guide.setId(id);
        guide.setSlug("pizza");
        return guide;
    }

    private static GuideAccessTokenEntity token(String guideId, Instant expiresAt) {
        GuideAccessTokenEntity token = new GuideAccessTokenEntity();
        token.setGuideId(guideId);
        token.setExpiresAt(expiresAt);
        return token;
    }
}
