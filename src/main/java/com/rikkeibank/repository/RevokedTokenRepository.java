package com.rikkeibank.repository;

import com.rikkeibank.entity.RevokedToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface RevokedTokenRepository extends JpaRepository<RevokedToken, Long> {
    boolean existsByToken(String token);

    void deleteByExpiredAtBefore(LocalDateTime time);
}
