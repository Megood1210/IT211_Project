package com.rikkeibank.controller;

import com.rikkeibank.dto.request.KycApprovalRequest;
import com.rikkeibank.dto.response.KycResponse;
import com.rikkeibank.enums.KycStatus;
import com.rikkeibank.service.KycService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/staff/kyc")
@RequiredArgsConstructor
public class StaffKycController {
    private final KycService kycService;

    @GetMapping
    public Page<KycResponse> getKycs(@RequestParam(required = false) KycStatus status,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        return kycService.getKycs(status, page, size);
    }

    @PutMapping("/{id}/approve")
    public KycResponse approve(@PathVariable Long id) {
        return kycService.approve(id);
    }

    @PutMapping("/{id}/reject")
    public KycResponse reject(@PathVariable Long id, @RequestBody @Valid KycApprovalRequest request) {
        return kycService.reject(id, request);
    }
}