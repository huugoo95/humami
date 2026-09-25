package com.hugo.humami.domain.guides;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Document(collection = "guides")
public class GuideEntity {
    @Id
    private String id;
    private String slug;
    private String title;
    private String excerpt;
    private String landingHeadline;
    private String landingDescription;
    private String coverImage;
    private String documentKey;
    private String duration;
    private String level;
    private Integer pageCount;
    private List<String> contents;
    private String status;
    private String seoTitle;
    private String seoDescription;
    private Instant publishedAt;
    private Instant createdAt;
    private Instant updatedAt;
}
