package com.rikkeibank.repository;

import com.rikkeibank.entity.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, Long> {
    boolean existsByAccessToken(String accessToken);

    void deleteByExpiryAtBefore(LocalDateTime now);
}