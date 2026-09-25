package com.hugo.humami.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AboutRequest {
    private String title;
    private List<String> story;
    private String photoUrl;
}
