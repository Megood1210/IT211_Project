package com.rikkeibank.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatementResponse {
    private Long transactionId;
    private String transactionCode;
    private String type;
    private BigDecimal amount;
    private String description;
    private String status;
    private String counterPartyAccount;
    private LocalDateTime createdAt;
}