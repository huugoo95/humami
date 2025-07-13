package com.hugo.humami.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class TimingsRequest {
    private Double prepTimeInHours;
    private Double totalTimeInHours;
}