package com.hugo.humami.service.impl;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;

@Component
public class GuideAccessRateLimiter {
    private static final Duration MINIMUM_INTERVAL = Duration.ofMinutes(1);
    private final ConcurrentHashMap<String, Instant> latestRequest = new ConcurrentHashMap<>();

    public void check(String email, String clientIp) {
        Instant now = Instant.now();
        checkKey("email:" + email, now);
        checkKey("ip:" + clientIp, now);
    }

    private void checkKey(String key, Instant now) {
        Instant previous = latestRequest.put(key, now);
        if (previous != null && previous.plus(MINIMUM_INTERVAL).isAfter(now)) {
            throw new ResponseStatusException(TOO_MANY_REQUESTS, "Please try again later");
        }
    }
}
