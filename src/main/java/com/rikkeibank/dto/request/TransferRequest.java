package com.rikkeibank.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequest {
    @NotBlank
    private String targetAccountNumber;

    @NotNull
    @DecimalMin(value = "1000")
    private BigDecimal amount;

    @Size(max = 255)
    private String description;

    @NotBlank
    @Size(min = 6, max = 6)
    private String transactionPin;
}