package com.rikkeibank.security;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginRateLimiter {
    private static final int MAX_REQUESTS = 5;
    private static final int BLOCK_MINUTES = 1;
    private final ConcurrentHashMap<String, AttemptInfo> attempts = new ConcurrentHashMap<>();

    public boolean allowRequest(String ip) {
        LocalDateTime now = LocalDateTime.now();

        AttemptInfo info = attempts.get(ip);

        if (info == null) {
            attempts.put(ip, new AttemptInfo(1, now));

            return true;
        }

        if (info.lastAttempt.plusMinutes(BLOCK_MINUTES).isBefore(now)) {
            attempts.put(ip, new AttemptInfo(1, now));

            return true;
        }

        if (info.count >= MAX_REQUESTS) {
            return false;
        }

        info.count++;
        info.lastAttempt = now;

        return true;
    }

    private static class AttemptInfo {
        int count;

        LocalDateTime lastAttempt;

        AttemptInfo(int count, LocalDateTime lastAttempt) {
            this.count = count;

            this.lastAttempt = lastAttempt;
        }
    }
}