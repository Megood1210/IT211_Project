package com.rikkeibank.dto.response;

import com.rikkeibank.enums.RoleName;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String phoneNumber;
    private RoleName role;
    private Boolean isActive;
}