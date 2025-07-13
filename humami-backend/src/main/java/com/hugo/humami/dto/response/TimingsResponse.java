package com.hugo.humami.dto.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class TimingsResponse {
    private Double prepTimeInHours;
    private Double totalTimeInHours;
}