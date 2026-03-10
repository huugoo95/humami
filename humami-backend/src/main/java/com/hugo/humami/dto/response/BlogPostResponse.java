package com.hugo.humami.dto.response;

import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class BlogPostResponse {
    private String id;
    private String slug;
    private String title;
    private String excerpt;
    private String content;
    private String coverImage;
    private String author;
    private List<String> tags;
    private String status;
    private String seoTitle;
    private String seoDescription;
    private Instant publishedAt;
    private Instant createdAt;
    private Instant updatedAt;
}
