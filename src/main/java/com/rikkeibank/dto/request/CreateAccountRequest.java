package com.rikkeibank.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateAccountRequest {
    @NotNull
    private Long userId;

    @NotBlank
    @Size(min = 6, max = 6)
    private String pin;

    @DecimalMin("0.0")
    private BigDecimal initialBalance;
}