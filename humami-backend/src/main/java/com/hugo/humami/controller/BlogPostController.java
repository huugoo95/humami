package com.hugo.humami.controller;

import com.hugo.humami.dto.request.BlogPostRequest;
import com.hugo.humami.dto.response.BlogPostResponse;
import com.hugo.humami.service.BlogPostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/blog")
public class BlogPostController {

    private final BlogPostService blogPostService;

    public BlogPostController(BlogPostService blogPostService) {
        this.blogPostService = blogPostService;
    }

    @GetMapping("")
    public ResponseEntity<List<BlogPostResponse>> listPublished() {
        return ResponseEntity.ok(blogPostService.listPublished());
    }

    @GetMapping("/{slug}")
    public ResponseEntity<BlogPostResponse> getBySlug(@PathVariable String slug) throws IOException {
        return ResponseEntity.ok(blogPostService.getPublishedBySlug(slug));
    }

    @PostMapping("")
    public ResponseEntity<BlogPostResponse> create(@RequestBody BlogPostRequest request) {
        return new ResponseEntity<>(blogPostService.create(request), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BlogPostResponse> update(@PathVariable String id, @RequestBody BlogPostRequest request) {
        return ResponseEntity.ok(blogPostService.update(id, request));
    }

    @PutMapping(value = "/{id}/cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> setCoverImage(@PathVariable String id, @RequestPart("image") MultipartFile image) throws IOException {
        blogPostService.setCoverImage(id, image);
        return ResponseEntity.ok().build();
    }
}
