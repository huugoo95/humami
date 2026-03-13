package com.hugo.humami.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PagedResponse<T> {
    private List<T> items;
    private int page;
    private int limit;
    private long totalItems;
    private int totalPages;
}
