package com.rikkeibank.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class KycApprovalRequest {
    @Size(max = 255)
    private String reason;
}