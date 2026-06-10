package com.rikkeibank.controller;

import com.rikkeibank.dto.response.ApiResponse;
import com.rikkeibank.dto.response.KycResponse;
import com.rikkeibank.service.KycService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/customer/kyc")
@RequiredArgsConstructor
public class CustomerKycController {
    private final KycService kycService;

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ApiResponse<KycResponse> uploadKyc(Authentication authentication,
                                              @RequestParam("file") MultipartFile file) {
        System.out.println(file.getOriginalFilename());

        KycResponse response = kycService.uploadKyc(authentication.getName(), file);

        return ApiResponse.<KycResponse>builder().status(200).message("KYC uploaded successfully")
                .data(response).build();
    }
}