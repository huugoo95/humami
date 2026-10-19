package com.hugo.humami.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface S3Service {
    /** Short-lived URL signed per call; use for private documents. */
    String getTempUrl(String key) throws IOException;

    /** URL for public images, reused across requests so browser and optimizer caches can hit. */
    String getImageUrl(String key) throws IOException;

    String uploadImage(MultipartFile image, String mealName) throws IOException;

    String uploadGuideDocument(MultipartFile document, String guideName) throws IOException;

    void deleteImage(String key);
}
