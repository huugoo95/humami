package com.hugo.humami.dto.request;

import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class GuideRequest {
    private String slug;
    private String title;
    private String excerpt;
    private String landingHeadline;
    private String landingDescription;
    private String duration;
    private String level;
    private Integer pageCount;
    private List<String> contents;
    private String status;
    private String seoTitle;
    private String seoDescription;
    private Instant publishedAt;
}
