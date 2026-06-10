package com.rikkeibank.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponse {
    private Long id;
    private String accountNumber;
    private BigDecimal balance;
    private Boolean active;
    private Long userId;
    private String username;
}