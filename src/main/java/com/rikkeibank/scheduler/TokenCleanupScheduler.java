package com.rikkeibank.scheduler;

import com.rikkeibank.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenCleanupScheduler {
    private final RevokedTokenRepository revokedTokenRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    @Scheduled(fixedRate = 3600000)
    public void cleanupTokens() {
        LocalDateTime now = LocalDateTime.now();

        revokedTokenRepository.deleteByExpiredAtBefore(now);

        refreshTokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());

        log.info("[SCHEDULER] Token cleanup completed");
    }
}