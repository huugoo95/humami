package com.hugo.humami.service.impl;

import com.hugo.humami.domain.AboutEntity;
import com.hugo.humami.dto.request.AboutRequest;
import com.hugo.humami.dto.response.AboutResponse;
import com.hugo.humami.repository.AboutRepository;
import com.hugo.humami.service.AboutService;
import com.hugo.humami.service.S3Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Service
public class AboutServiceImpl implements AboutService {

    private static final String ABOUT_MAIN_ID = "main";

    private final AboutRepository aboutRepository;
    private final S3Service s3Service;

    private final String fallbackTitle;
    private final String fallbackStoryLine1;
    private final String fallbackStoryLine2;
    private final String fallbackStoryLine3;
    private final String fallbackPhotoUrl;
    private final String fallbackUpdatedAt;

    public AboutServiceImpl(
            AboutRepository aboutRepository,
            S3Service s3Service,
            @Value("${humami.about.title:La historia detrás de Humami}") String fallbackTitle,
            @Value("${humami.about.story-line-1:Humami nace de una mezcla personal entre tecnología y cocina de raíz.}") String fallbackStoryLine1,
            @Value("${humami.about.story-line-2:Detrás del proyecto está Hugo, developer e hijo de profesora de cocina.}") String fallbackStoryLine2,
            @Value("${humami.about.story-line-3:Desde Galicia, Humami une método y sabor para cocinar mejor cada día.}") String fallbackStoryLine3,
            @Value("${humami.about.photo-url:}") String fallbackPhotoUrl,
            @Value("${humami.about.updated-at:2026-03-19T00:00:00Z}") String fallbackUpdatedAt
    ) {
        this.aboutRepository = aboutRepository;
        this.s3Service = s3Service;
        this.fallbackTitle = fallbackTitle;
        this.fallbackStoryLine1 = fallbackStoryLine1;
        this.fallbackStoryLine2 = fallbackStoryLine2;
        this.fallbackStoryLine3 = fallbackStoryLine3;
        this.fallbackPhotoUrl = fallbackPhotoUrl;
        this.fallbackUpdatedAt = fallbackUpdatedAt;
    }

    @Override
    public AboutResponse getAbout() {
        AboutEntity entity = aboutRepository.findById(ABOUT_MAIN_ID).orElseGet(this::createSeedAboutEntity);
        return toResponse(entity);
    }

    @Override
    public AboutResponse updateAbout(AboutRequest request) {
        AboutEntity entity = aboutRepository.findById(ABOUT_MAIN_ID).orElseGet(this::createSeedAboutEntity);

        if (request.getTitle() != null) {
            entity.setTitle(request.getTitle().trim());
        }
        if (request.getStory() != null) {
            List<String> cleanedStory = request.getStory().stream()
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(line -> !line.isBlank())
                    .toList();
            entity.setStory(cleanedStory);
        }
        if (request.getPhotoUrl() != null) {
            entity.setPhotoUrl(request.getPhotoUrl().trim());
        }

        entity.setUpdatedAt(Instant.now());

        AboutEntity saved = aboutRepository.save(entity);
        return toResponse(saved);
    }

    @Override
    public AboutResponse updateAboutImage(MultipartFile image) throws IOException {
        AboutEntity entity = aboutRepository.findById(ABOUT_MAIN_ID).orElseGet(this::createSeedAboutEntity);

        if (image != null && !image.isEmpty()) {
            String imageKey = s3Service.uploadImage(image, "about-humami");
            entity.setPhotoUrl(imageKey);
            entity.setUpdatedAt(Instant.now());
            entity = aboutRepository.save(entity);
        }

        return toResponse(entity);
    }

    private AboutEntity createSeedAboutEntity() {
        AboutEntity seed = new AboutEntity();
        seed.setId(ABOUT_MAIN_ID);
        seed.setTitle(fallbackTitle);
        seed.setStory(List.of(fallbackStoryLine1, fallbackStoryLine2, fallbackStoryLine3));
        seed.setPhotoUrl(fallbackPhotoUrl);
        seed.setUpdatedAt(parseOrNow(fallbackUpdatedAt));

        return aboutRepository.save(seed);
    }

    private AboutResponse toResponse(AboutEntity entity) {
        String resolvedPhoto = resolvePhotoUrl(entity.getPhotoUrl());

        return new AboutResponse(
                entity.getTitle(),
                entity.getStory() == null ? List.of() : entity.getStory(),
                resolvedPhoto,
                entity.getUpdatedAt() == null ? Instant.now() : entity.getUpdatedAt()
        );
    }

    private String resolvePhotoUrl(String storedPhoto) {
        if (storedPhoto == null || storedPhoto.isBlank()) {
            return "";
        }

        if (storedPhoto.startsWith("http://") || storedPhoto.startsWith("https://")) {
            return storedPhoto;
        }

        try {
            return s3Service.getTempUrl(storedPhoto);
        } catch (IOException ignored) {
            return "";
        }
    }

    private Instant parseOrNow(String instantText) {
        try {
            return Instant.parse(instantText);
        } catch (Exception ignored) {
            return Instant.now();
        }
    }
}
