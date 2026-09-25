package com.hugo.humami.service;

import com.hugo.humami.dto.request.BlogPostRequest;
import com.hugo.humami.dto.response.BlogPostResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface BlogPostService {
    List<BlogPostResponse> listPublished();
    BlogPostResponse getPublishedBySlug(String slug) throws IOException;
    BlogPostResponse create(BlogPostRequest request);
    BlogPostResponse update(String id, BlogPostRequest request);
    void setCoverImage(String id, MultipartFile image) throws IOException;
}
