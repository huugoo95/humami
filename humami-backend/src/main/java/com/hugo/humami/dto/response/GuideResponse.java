package com.hugo.humami.dto.response;

import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class GuideResponse {
    private String id;
    private String slug;
    private String title;
    private String excerpt;
    private String landingHeadline;
    private String landingDescription;
    private String coverImage;
    private String duration;
    private String level;
    private Integer pageCount;
    private List<String> contents;
    private String seoTitle;
    private String seoDescription;
    private Instant publishedAt;
}
