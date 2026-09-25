package com.hugo.humami.domain.guides;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document(collection = "guide_access_tokens")
public class GuideAccessTokenEntity {
    @Id
    private String id;
    @Indexed(unique = true)
    private String tokenHash;
    private String guideId;
    private String leadId;
    private Instant expiresAt;
    private Instant redeemedAt;
    private Instant revokedAt;
    private Instant createdAt;
}
