package com.hugo.humami.service;

import com.hugo.humami.dto.request.GuideAccessRequest;
import com.hugo.humami.dto.request.GuideRequest;
import com.hugo.humami.dto.response.GuideDocumentResponse;
import com.hugo.humami.dto.response.GuideResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface GuideService {
    List<GuideResponse> listPublished();
    GuideResponse getPublishedBySlug(String slug) throws IOException;
    GuideResponse create(GuideRequest request);
    GuideResponse update(String id, GuideRequest request);
    void setCoverImage(String id, MultipartFile image) throws IOException;
    void setDocument(String id, MultipartFile document) throws IOException;
    String requestAccess(String slug, GuideAccessRequest request, String clientIp);
    String redeem(String slug, String token);
    GuideDocumentResponse getDocument(String slug, String token) throws IOException;
}
