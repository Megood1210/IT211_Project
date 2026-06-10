package com.rikkeibank.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KycResponse {
    private Long id;
    private String documentUrl;
    private String status;
    private String reason;
    private Long userId;
    private String username;
}