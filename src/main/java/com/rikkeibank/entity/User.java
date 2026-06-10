package com.rikkeibank.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true)
    private String phoneNumber;

    @Column(unique = true)
    private String email;

    @Builder.Default
    private Boolean isActive = true;

    @Builder.Default
    private Boolean isKyc = false;

    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToMany(mappedBy = "user")
    private List<Account> accounts;

    @OneToOne(mappedBy = "user")
    private KycProfile kycProfile;

    @OneToMany(mappedBy = "user")
    private List<RefreshToken> refreshTokens;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    @Column(unique = true)
    private String resetPasswordToken;

    private LocalDateTime resetPasswordExpiredAt;
}