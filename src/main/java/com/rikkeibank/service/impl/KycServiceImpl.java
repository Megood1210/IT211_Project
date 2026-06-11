package com.rikkeibank.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.rikkeibank.dto.request.KycApprovalRequest;
import com.rikkeibank.dto.response.KycResponse;
import com.rikkeibank.entity.KycProfile;
import com.rikkeibank.entity.User;
import com.rikkeibank.enums.KycStatus;
import com.rikkeibank.exception.*;
import com.rikkeibank.repository.KycProfileRepository;
import com.rikkeibank.repository.UserRepository;
import com.rikkeibank.service.KycService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KycServiceImpl implements KycService {
    private final Cloudinary cloudinary;
    private final UserRepository userRepository;
    private final KycProfileRepository kycRepository;

    @Override
    public KycResponse uploadKyc(String username, MultipartFile file) {
        if (file.isEmpty()) {
            throw new FileUploadException("File is empty");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("File exceeds 5MB");
        }

        String contentType = file.getContentType();

        if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
            throw new IllegalArgumentException("Only JPG/PNG allowed");
        }


        if (contentType == null || (!contentType.equals("image/jpeg") && !contentType
                .equals("image/png"))) {
            throw new FileUploadException("Only PNG/JPG allowed");
        }

        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new ResourceNotFoundException("User not found"));

        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());

            String secureUrl = uploadResult.get("secure_url").toString();

            KycProfile kyc = kycRepository.findByUserId(user.getId()).orElse(KycProfile.builder().user(user).build());

            kyc.setDocumentUrl(secureUrl);

            KycProfile saved = kycRepository.save(kyc);

            return KycResponse.builder().id(saved.getId()).documentUrl(saved.getDocumentUrl()).status(saved.getStatus().name()).userId(user.getId()).username(user.getUsername()).build();

        } catch (IOException e) {
            throw new FileUploadException("Cloud upload failed");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<KycResponse> getKycs(KycStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<KycProfile> kycs;

        if (status == null) {
            kycs = kycRepository.findAll(pageable);
        } else {

            kycs = kycRepository.findByStatus(status, pageable);
        }

        return kycs.map(kyc -> KycResponse.builder().id(kyc.getId())
                .documentUrl(kyc.getDocumentUrl()).status(kyc.getStatus().name()).reason(kyc
                        .getReason()).userId(kyc.getUser().getId()).username(kyc.getUser()
                        .getUsername()).build());
    }

    @Override
    @Transactional
    public KycResponse approve(Long id) {
        KycProfile kyc = kycRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("KYC not found"));

        kyc.setStatus(KycStatus.APPROVED);

        kyc.setReason(null);

        KycProfile saved = kycRepository.save(kyc);

        return KycResponse.builder().id(saved.getId()).documentUrl(saved.getDocumentUrl())
                .status(saved.getStatus().name()).reason(saved.getReason()).userId(saved.getUser()
                        .getId()).username(saved.getUser().getUsername()).build();
    }

    @Override
    public KycResponse reject(Long id, KycApprovalRequest request) {
        KycProfile kyc = kycRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("KYC not found"));

        kyc.setStatus(KycStatus.REJECTED);

        kyc.setReason(request.getReason());

        KycProfile saved = kycRepository.save(kyc);

        return KycResponse.builder().id(saved.getId()).documentUrl(saved.getDocumentUrl())
                .status(saved.getStatus().name()).reason(saved.getReason()).userId(saved.getUser()
                        .getId()).username(saved.getUser().getUsername()).build();
    }
}