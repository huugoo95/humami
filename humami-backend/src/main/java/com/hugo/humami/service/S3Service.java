package com.hugo.humami.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface S3Service {
    String getTempUrl(String key) throws IOException;

    String uploadImage(MultipartFile image) throws IOException;
}