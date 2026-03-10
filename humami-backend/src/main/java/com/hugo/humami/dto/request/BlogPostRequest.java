package com.hugo.humami.dto.request;

import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class BlogPostRequest {
    private String slug;
    private String title;
    private String excerpt;
    private String content;
    private String author;
    private List<String> tags;
    private String status;
    private String seoTitle;
    private String seoDescription;
    private Instant publishedAt;
}
