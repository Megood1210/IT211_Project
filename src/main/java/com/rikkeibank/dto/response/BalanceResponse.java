package com.rikkeibank.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class BalanceResponse {
    private String accountNumber;
    private BigDecimal balance;
    private String currency;
}