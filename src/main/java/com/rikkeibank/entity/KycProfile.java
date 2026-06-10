package com.rikkeibank.entity;

import com.rikkeibank.enums.KycStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "kyc_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KycProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255)
    private String reason;

    @Column(nullable = false)
    private String documentUrl;

    @Enumerated(EnumType.STRING)
    private KycStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();

        this.status = KycStatus.PENDING;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}