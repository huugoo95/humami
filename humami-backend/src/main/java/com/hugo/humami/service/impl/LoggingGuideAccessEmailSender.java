package com.hugo.humami.service.impl;

import com.hugo.humami.service.GuideAccessEmailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "humami.guides.email", name = "enabled", havingValue = "false", matchIfMissing = true)
public class LoggingGuideAccessEmailSender implements GuideAccessEmailSender {
    private static final Logger LOG = LoggerFactory.getLogger(LoggingGuideAccessEmailSender.class);

    @Override
    public void sendAccessEmail(String recipient, String guideTitle, String accessUrl) {
        LOG.warn("Guide email delivery is not configured; access email for guide '{}' was not sent to {}", guideTitle, recipient);
    }
}
