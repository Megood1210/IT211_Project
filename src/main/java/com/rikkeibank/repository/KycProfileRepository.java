package com.rikkeibank.repository;

import com.rikkeibank.entity.KycProfile;
import com.rikkeibank.enums.KycStatus;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;

import java.util.List;
import java.util.Optional;

public interface KycProfileRepository extends JpaRepository<KycProfile, Long> {
    Optional<KycProfile> findByUserId(Long userId);

    List<KycProfile> findByStatus(KycStatus status);

    Page<KycProfile>
    findByStatus(KycStatus status, Pageable pageable);
}