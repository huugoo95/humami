package com.hugo.humami.domain.blog;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "blog_posts")
public class BlogPostEntity {
    @Id
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
