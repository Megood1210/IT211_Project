package com.rikkeibank.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterResponse {
    private Long userId;
    private String username;
    private String accountNumber;
    private BigDecimal balance;
    private String status;
}