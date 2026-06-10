package com.rikkeibank.service;

import com.rikkeibank.dto.request.KycApprovalRequest;
import com.rikkeibank.dto.response.KycResponse;
import com.rikkeibank.enums.KycStatus;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface KycService {
    KycResponse uploadKyc(String username, MultipartFile file);
    Page<KycResponse> getKycs(KycStatus status, int page, int size);
    KycResponse approve(Long id);
    KycResponse reject(Long id, KycApprovalRequest request);
}