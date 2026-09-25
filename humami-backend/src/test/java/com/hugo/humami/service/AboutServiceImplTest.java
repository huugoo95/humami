package com.hugo.humami.service;

import com.hugo.humami.domain.AboutEntity;
import com.hugo.humami.dto.request.AboutRequest;
import com.hugo.humami.dto.response.AboutResponse;
import com.hugo.humami.repository.AboutRepository;
import com.hugo.humami.service.impl.AboutServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AboutServiceImplTest {

    @Test
    void getAboutShouldReturnPersistedRecordWhenExists() throws Exception {
        AboutRepository aboutRepository = mock(AboutRepository.class);
        S3Service s3Service = mock(S3Service.class);

        AboutEntity entity = new AboutEntity();
        entity.setId("main");
        entity.setTitle("Persisted title");
        entity.setStory(List.of("S1", "S2"));
        entity.setPhotoUrl("about/photo-key.jpg");
        entity.setUpdatedAt(Instant.parse("2026-03-19T00:00:00Z"));

        when(aboutRepository.findById("main")).thenReturn(Optional.of(entity));
        when(s3Service.getTempUrl("about/photo-key.jpg")).thenReturn("https://signed.example.com/about/photo-key.jpg");

        AboutServiceImpl service = new AboutServiceImpl(
                aboutRepository,
                s3Service,
                "Fallback",
                "F1",
                "F2",
                "F3",
                "",
                "2026-03-19T00:00:00Z"
        );

        AboutResponse response = service.getAbout();

        assertEquals("Persisted title", response.getTitle());
        assertEquals("S1", response.getStory().get(0));
        assertEquals("https://signed.example.com/about/photo-key.jpg", response.getPhotoUrl());
    }

    @Test
    void updateAboutShouldSaveChangesAndSetUpdatedAt() {
        AboutRepository aboutRepository = mock(AboutRepository.class);
        S3Service s3Service = mock(S3Service.class);

        AboutEntity existing = new AboutEntity();
        existing.setId("main");
        existing.setTitle("Old");
        existing.setStory(List.of("Old line"));
        existing.setPhotoUrl("");
        existing.setUpdatedAt(Instant.parse("2026-03-19T00:00:00Z"));

        when(aboutRepository.findById("main")).thenReturn(Optional.of(existing));
        when(aboutRepository.save(any(AboutEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        AboutServiceImpl service = new AboutServiceImpl(
                aboutRepository,
                s3Service,
                "Fallback",
                "F1",
                "F2",
                "F3",
                "",
                "2026-03-19T00:00:00Z"
        );

        AboutResponse response = service.updateAbout(new AboutRequest(
                "Nuevo título",
                List.of(" Línea 1 ", "", "Línea 2"),
                "https://cdn.example.com/new.jpg"
        ));

        assertEquals("Nuevo título", response.getTitle());
        assertEquals(2, response.getStory().size());
        assertEquals("Línea 1", response.getStory().get(0));
        assertEquals("https://cdn.example.com/new.jpg", response.getPhotoUrl());
        assertNotNull(response.getUpdatedAt());

        verify(aboutRepository).save(any(AboutEntity.class));
    }

    @Test
    void updateAboutImageShouldUploadAndPersistKey() throws Exception {
        AboutRepository aboutRepository = mock(AboutRepository.class);
        S3Service s3Service = mock(S3Service.class);

        AboutEntity existing = new AboutEntity();
        existing.setId("main");
        existing.setTitle("Old");
        existing.setStory(List.of("Line"));
        existing.setPhotoUrl("");
        existing.setUpdatedAt(Instant.parse("2026-03-19T00:00:00Z"));

        when(aboutRepository.findById("main")).thenReturn(Optional.of(existing));
        when(aboutRepository.save(any(AboutEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        when(s3Service.uploadImage(any(), any())).thenReturn("about/new-photo.jpg");
        when(s3Service.getTempUrl("about/new-photo.jpg")).thenReturn("https://signed.example.com/about/new-photo.jpg");

        AboutServiceImpl service = new AboutServiceImpl(
                aboutRepository,
                s3Service,
                "Fallback",
                "F1",
                "F2",
                "F3",
                "",
                "2026-03-19T00:00:00Z"
        );

        MockMultipartFile image = new MockMultipartFile("image", "photo.jpg", "image/jpeg", "abc".getBytes());
        AboutResponse response = service.updateAboutImage(image);

        assertEquals("https://signed.example.com/about/new-photo.jpg", response.getPhotoUrl());
        assertNotNull(response.getUpdatedAt());

        verify(s3Service).uploadImage(any(), any());
        verify(aboutRepository).save(any(AboutEntity.class));
    }
}
