package com.hugo.humami.service.impl;

import com.hugo.humami.domain.blog.BlogPostEntity;
import com.hugo.humami.dto.request.BlogPostRequest;
import com.hugo.humami.dto.response.BlogPostResponse;
import com.hugo.humami.repository.BlogPostRepository;
import com.hugo.humami.service.BlogPostService;
import com.hugo.humami.service.S3Service;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@Service
public class BlogPostServiceImpl implements BlogPostService {

    private static final String PUBLISHED = "published";

    private final BlogPostRepository blogPostRepository;
    private final S3Service s3Service;

    public BlogPostServiceImpl(BlogPostRepository blogPostRepository, S3Service s3Service) {
        this.blogPostRepository = blogPostRepository;
        this.s3Service = s3Service;
    }

    @Override
    public List<BlogPostResponse> listPublished() {
        return blogPostRepository.findByStatusOrderByPublishedAtDesc(PUBLISHED)
                .stream().map(this::toResponse).toList();
    }

    @Override
    public BlogPostResponse getPublishedBySlug(String slug) throws IOException {
        BlogPostEntity post = blogPostRepository.findBySlugAndStatus(slug, PUBLISHED)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Blog post not found"));
        BlogPostResponse response = toResponse(post);
        if (post.getCoverImage() != null && !post.getCoverImage().isBlank()) {
            response.setCoverImage(s3Service.getTempUrl(post.getCoverImage()));
        }
        return response;
    }

    @Override
    public BlogPostResponse create(BlogPostRequest request) {
        if (request.getSlug() == null || request.getSlug().isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "slug is required");
        }
        if (blogPostRepository.existsBySlug(request.getSlug())) {
            throw new ResponseStatusException(CONFLICT, "slug already exists");
        }

        BlogPostEntity entity = new BlogPostEntity();
        applyRequest(entity, request);
        Instant now = Instant.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        return toResponse(blogPostRepository.save(entity));
    }

    @Override
    public BlogPostResponse update(String id, BlogPostRequest request) {
        BlogPostEntity entity = blogPostRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Blog post not found"));

        if (request.getSlug() != null && !request.getSlug().equals(entity.getSlug()) && blogPostRepository.existsBySlug(request.getSlug())) {
            throw new ResponseStatusException(CONFLICT, "slug already exists");
        }

        applyRequest(entity, request);
        entity.setUpdatedAt(Instant.now());
        return toResponse(blogPostRepository.save(entity));
    }

    @Override
    public void setCoverImage(String id, MultipartFile image) throws IOException {
        BlogPostEntity entity = blogPostRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Blog post not found"));

        if (image != null && !image.isEmpty()) {
            String imageKey = s3Service.uploadImage(image, "blog-" + entity.getSlug());
            entity.setCoverImage(imageKey);
            entity.setUpdatedAt(Instant.now());
            blogPostRepository.save(entity);
        }
    }

    private void applyRequest(BlogPostEntity entity, BlogPostRequest request) {
        if (request.getSlug() != null) entity.setSlug(request.getSlug());
        if (request.getTitle() != null) entity.setTitle(request.getTitle());
        if (request.getExcerpt() != null) entity.setExcerpt(request.getExcerpt());
        if (request.getContent() != null) entity.setContent(request.getContent());
        if (request.getAuthor() != null) entity.setAuthor(request.getAuthor());
        if (request.getTags() != null) entity.setTags(request.getTags());
        if (request.getStatus() != null) entity.setStatus(request.getStatus());
        if (request.getSeoTitle() != null) entity.setSeoTitle(request.getSeoTitle());
        if (request.getSeoDescription() != null) entity.setSeoDescription(request.getSeoDescription());
        if (request.getPublishedAt() != null) entity.setPublishedAt(request.getPublishedAt());
    }

    private BlogPostResponse toResponse(BlogPostEntity entity) {
        BlogPostResponse response = new BlogPostResponse();
        response.setId(entity.getId());
        response.setSlug(entity.getSlug());
        response.setTitle(entity.getTitle());
        response.setExcerpt(entity.getExcerpt());
        response.setContent(entity.getContent());
        response.setCoverImage(entity.getCoverImage());
        response.setAuthor(entity.getAuthor());
        response.setTags(entity.getTags());
        response.setStatus(entity.getStatus());
        response.setSeoTitle(entity.getSeoTitle());
        response.setSeoDescription(entity.getSeoDescription());
        response.setPublishedAt(entity.getPublishedAt());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        return response;
    }
}
