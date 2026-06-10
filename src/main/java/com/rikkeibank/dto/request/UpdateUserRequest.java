package com.rikkeibank.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateUserRequest {
    @Email
    private String email;

    @Pattern(regexp = "^(0)[0-9]{9}$")
    private String phoneNumber;

    private Boolean isActive;
}