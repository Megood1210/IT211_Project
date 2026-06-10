package com.rikkeibank.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {
    private Long id;
    private String transactionCode;
    private String fromAccount;
    private String toAccount;
    private BigDecimal amount;
    private String description;
    private String status;
    private LocalDateTime createdAt;
}