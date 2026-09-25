package com.hugo.humami.service.impl;

import com.hugo.humami.domain.blog.BlogPostEntity;
import com.hugo.humami.dto.request.BlogPostRequest;
import com.hugo.humami.dto.response.BlogPostResponse;
import com.hugo.humami.mapper.BlogPostMapper;
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
    private final BlogPostMapper blogPostMapper;

    public BlogPostServiceImpl(BlogPostRepository blogPostRepository, S3Service s3Service, BlogPostMapper blogPostMapper) {
        this.blogPostRepository = blogPostRepository;
        this.s3Service = s3Service;
        this.blogPostMapper = blogPostMapper;
    }

    @Override
    public List<BlogPostResponse> listPublished() {
        return blogPostRepository.findByStatusOrderByPublishedAtDesc(PUBLISHED)
                .stream().map(blogPostMapper::toResponse).toList();
    }

    @Override
    public BlogPostResponse getPublishedBySlug(String slug) throws IOException {
        BlogPostEntity post = blogPostRepository.findBySlugAndStatus(slug, PUBLISHED)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Blog post not found"));
        BlogPostResponse response = blogPostMapper.toResponse(post);
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

        BlogPostEntity entity = blogPostMapper.toEntity(request);
        Instant now = Instant.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        return blogPostMapper.toResponse(blogPostRepository.save(entity));
    }

    @Override
    public BlogPostResponse update(String id, BlogPostRequest request) {
        BlogPostEntity entity = blogPostRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Blog post not found"));

        if (request.getSlug() != null && !request.getSlug().equals(entity.getSlug()) && blogPostRepository.existsBySlug(request.getSlug())) {
            throw new ResponseStatusException(CONFLICT, "slug already exists");
        }

        blogPostMapper.updateFromRequest(entity, request);
        entity.setUpdatedAt(Instant.now());
        return blogPostMapper.toResponse(blogPostRepository.save(entity));
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

}
