package com.rikkeibank.scheduler;

import com.rikkeibank.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@EnableScheduling
public class TokenCleanupScheduler {
    private final TokenBlacklistRepository tokenBlacklistRepository;

    @Scheduled(cron = "0 0 * * * *")
    public void cleanupExpiredTokens() {
        tokenBlacklistRepository.deleteByExpiryAtBefore(LocalDateTime.now());

        System.out.println("Expired blacklist tokens cleaned");
    }
}