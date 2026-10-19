package com.hugo.humami.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class S3ServiceImplTest {

    private static final String MEDIA_BUCKET = "humami-media-eu";
    private static final String PRIVATE_BUCKET = "humami-private-eu";
    private static final String PUBLIC_BASE_URL = "https://img.humami.es";

    private final MutableClock clock = new MutableClock(Instant.parse("2026-10-19T10:00:00Z"));
    private final AtomicInteger signatures = new AtomicInteger();
    private S3Client s3Client;
    private S3Presigner presigner;

    @BeforeEach
    void setUp() throws Exception {
        s3Client = mock(S3Client.class);
        presigner = mock(S3Presigner.class);
        when(presigner.presignGetObject(any(GetObjectPresignRequest.class))).thenAnswer(invocation -> {
            GetObjectPresignRequest request = invocation.getArgument(0);
            PresignedGetObjectRequest presigned = mock(PresignedGetObjectRequest.class);
            when(presigned.url()).thenReturn(new URL("https://" + request.getObjectRequest().bucket() + ".s3.amazonaws.com/"
                    + request.getObjectRequest().key() + "?expires=" + request.signatureDuration().toHours() + "h&sig=" + signatures.incrementAndGet()));
            return presigned;
        });
    }

    private S3ServiceImpl service(String publicBaseUrl) {
        return new S3ServiceImpl(s3Client, presigner, MEDIA_BUCKET, PRIVATE_BUCKET, publicBaseUrl, clock);
    }

    @Test
    void servesImagesFromTheStablePublicCdnUrlWithoutSigning() {
        S3ServiceImpl service = service(PUBLIC_BASE_URL + "/");

        String first = service.getImageUrl("meal_123.jpg");
        clock.advance(Duration.ofDays(30));

        assertThat(first).isEqualTo("https://img.humami.es/meal_123.jpg");
        assertThat(service.getImageUrl("meal_123.jpg")).isEqualTo(first);
        verify(presigner, never()).presignGetObject(any(GetObjectPresignRequest.class));
    }

    @Test
    void percentEncodesEachKeySegmentAndKeepsSlashes() {
        assertThat(service(PUBLIC_BASE_URL).getImageUrl("about/lasaña con queso_1.jpg"))
                .isEqualTo("https://img.humami.es/about/lasa%C3%B1a%20con%20queso_1.jpg");
    }

    @Test
    void refusesToExposePrivateKeysThroughThePublicCdn() {
        assertThatThrownBy(() -> service(PUBLIC_BASE_URL).getImageUrl("private/guides/guide.pdf"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void fallsBackToReusedPresignedMediaUrlsWhenNoPublicBaseUrlIsConfigured() {
        S3ServiceImpl service = service("");

        String first = service.getImageUrl("meal.jpg");
        clock.advance(Duration.ofHours(5));

        assertThat(service.getImageUrl("meal.jpg")).isEqualTo(first).startsWith("https://" + MEDIA_BUCKET).contains("expires=12h");
        clock.advance(Duration.ofHours(1).plusSeconds(1));
        assertThat(service.getImageUrl("meal.jpg")).isNotEqualTo(first);
        verify(presigner, times(2)).presignGetObject(any(GetObjectPresignRequest.class));
    }

    @Test
    void keepsPrivateDocumentUrlsShortLivedUnsharedAndInThePrivateBucket() {
        S3ServiceImpl service = service(PUBLIC_BASE_URL);

        String first = service.getTempUrl("private/guides/guide.pdf");

        assertThat(first).startsWith("https://" + PRIVATE_BUCKET).contains("expires=1h");
        assertThat(service.getTempUrl("private/guides/guide.pdf")).isNotEqualTo(first);
    }

    @Test
    void uploadsImagesToTheMediaBucketWithImmutableCaching() throws Exception {
        service(PUBLIC_BASE_URL).uploadImage(new MockMultipartFile("image", "a.png", "image/png", png()), "Tarta de queso");

        ArgumentCaptor<PutObjectRequest> request = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client).putObject(request.capture(), any(RequestBody.class));
        assertThat(request.getValue().bucket()).isEqualTo(MEDIA_BUCKET);
        assertThat(request.getValue().key()).startsWith("Tarta_de_queso_").endsWith(".jpg");
        assertThat(request.getValue().contentType()).isEqualTo("image/jpeg");
        assertThat(request.getValue().cacheControl()).isEqualTo("public, max-age=31536000, immutable");
    }

    @Test
    void uploadsGuideDocumentsToThePrivateBucket() throws Exception {
        service(PUBLIC_BASE_URL).uploadGuideDocument(new MockMultipartFile("document", "g.pdf", "application/pdf", new byte[]{1, 2, 3}), "Pizza");

        ArgumentCaptor<PutObjectRequest> request = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client).putObject(request.capture(), any(RequestBody.class));
        assertThat(request.getValue().bucket()).isEqualTo(PRIVATE_BUCKET);
        assertThat(request.getValue().key()).startsWith("private/guides/Pizza_");
    }

    @Test
    void deletesImagesFromTheMediaBucket() {
        service(PUBLIC_BASE_URL).deleteImage("meal.jpg");

        ArgumentCaptor<DeleteObjectRequest> request = ArgumentCaptor.forClass(DeleteObjectRequest.class);
        verify(s3Client).deleteObject(request.capture());
        assertThat(request.getValue().bucket()).isEqualTo(MEDIA_BUCKET);
    }

    private static byte[] png() throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ImageIO.write(new BufferedImage(32, 18, BufferedImage.TYPE_INT_RGB), "png", bytes);
        return bytes.toByteArray();
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
