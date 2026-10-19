package com.hugo.humami.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.net.URL;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class S3ServiceImplTest {

    private final MutableClock clock = new MutableClock(Instant.parse("2026-10-19T10:00:00Z"));
    private final AtomicInteger signatures = new AtomicInteger();
    private S3Presigner presigner;
    private S3ServiceImpl service;

    @BeforeEach
    void setUp() throws Exception {
        presigner = mock(S3Presigner.class);
        when(presigner.presignGetObject(any(GetObjectPresignRequest.class))).thenAnswer(invocation -> {
            GetObjectPresignRequest request = invocation.getArgument(0);
            PresignedGetObjectRequest presigned = mock(PresignedGetObjectRequest.class);
            when(presigned.url()).thenReturn(new URL("https://humami.s3.amazonaws.com/" + request.getObjectRequest().key()
                    + "?expires=" + request.signatureDuration().toHours() + "h&sig=" + signatures.incrementAndGet()));
            return presigned;
        });
        service = new S3ServiceImpl(mock(S3Client.class), presigner, clock);
    }

    @Test
    void reusesTheSameImageUrlWithinTheReuseWindowSoCachesCanHit() throws Exception {
        String first = service.getImageUrl("meal.jpg");
        clock.advance(Duration.ofHours(5));
        String second = service.getImageUrl("meal.jpg");

        assertThat(second).isEqualTo(first).contains("expires=12h");
        verify(presigner, times(1)).presignGetObject(any(GetObjectPresignRequest.class));
    }

    @Test
    void signsANewImageUrlOnceTheReuseWindowEndsSoItNeverServesANearlyExpiredUrl() throws Exception {
        String first = service.getImageUrl("meal.jpg");
        clock.advance(Duration.ofHours(6).plusSeconds(1));
        String second = service.getImageUrl("meal.jpg");

        assertThat(second).isNotEqualTo(first);
    }

    @Test
    void keepsImageUrlsSeparatePerKey() throws Exception {
        assertThat(service.getImageUrl("a.jpg")).isNotEqualTo(service.getImageUrl("b.jpg"));
    }

    @Test
    void keepsPrivateDocumentUrlsShortLivedAndUnshared() throws Exception {
        String first = service.getTempUrl("private/guides/guide.pdf");
        String second = service.getTempUrl("private/guides/guide.pdf");

        assertThat(first).contains("expires=1h");
        assertThat(second).isNotEqualTo(first);
    }

    private static final class MutableClock extends Clock {
        private Instant now;

        private MutableClock(Instant now) {
            this.now = now;
        }

        void advance(Duration duration) {
            now = now.plus(duration);
        }

        @Override
        public ZoneOffset getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(java.time.ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return now;
        }
    }
}
