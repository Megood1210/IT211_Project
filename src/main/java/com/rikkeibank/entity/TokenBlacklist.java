package com.rikkeibank.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "token_blacklist")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenBlacklist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(nullable = false)
    private String accessToken;

    private LocalDateTime expiryAt;

    private LocalDateTime blacklistedAt;

    @PrePersist
    public void prePersist() {
        this.blacklistedAt = LocalDateTime.now();
    }
}