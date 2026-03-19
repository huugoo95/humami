package com.hugo.humami.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AboutResponse {
    private String title;
    private List<String> story;
    private String photoUrl;
    private Instant updatedAt;
}
