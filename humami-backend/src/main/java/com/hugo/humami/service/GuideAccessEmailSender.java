package com.hugo.humami.service;

public interface GuideAccessEmailSender {
    void sendAccessEmail(String recipient, String guideTitle, String accessUrl);
}
