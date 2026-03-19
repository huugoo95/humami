package com.hugo.humami.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "about_pages")
public class AboutEntity {
    @Id
    private String id;
    private String title;
    private List<String> story;
    private String photoUrl;
    private Instant updatedAt;
}
