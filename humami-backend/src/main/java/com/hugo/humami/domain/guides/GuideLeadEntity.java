package com.hugo.humami.domain.guides;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document(collection = "guide_leads")
@CompoundIndex(name = "guide_email_unique", def = "{'guideId': 1, 'email': 1}", unique = true)
public class GuideLeadEntity {
    @Id
    private String id;
    private String guideId;
    private String email;
    private String privacyNoticeVersion;
    private Instant deliveryAcknowledgedAt;
    private boolean marketingOptIn;
    private Instant marketingOptInAt;
    private String source;
    private Instant createdAt;
    private Instant updatedAt;
}
