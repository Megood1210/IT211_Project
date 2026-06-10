package com.rikkeibank.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {
    @NotBlank
    private String fullName;

    @NotBlank
    private String username;

    @Email
    private String email;

    private String phone;

    @NotBlank
    private String password;

    @NotBlank
    private String confirmPassword;

    @Pattern(regexp = "\\d{6}", message = "PIN must be 6 digits")
    private String transactionPin;
}