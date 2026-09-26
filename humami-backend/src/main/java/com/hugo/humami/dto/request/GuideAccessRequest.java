package com.hugo.humami.dto.request;

import lombok.Data;

@Data
public class GuideAccessRequest {
    private String email;
    private String privacyNoticeVersion;
    private boolean privacyAccepted;
    private boolean marketingOptIn;
    private String source;
    private String website;
}
