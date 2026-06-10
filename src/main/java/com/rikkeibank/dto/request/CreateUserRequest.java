package com.rikkeibank.dto.request;

import com.rikkeibank.enums.RoleName;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateUserRequest {
    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 50)
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6)
    private String password;

    @Email(message = "Invalid email")
    private String email;

    @Pattern(regexp = "^(0)[0-9]{9}$", message = "Invalid phone number")
    private String phoneNumber;

    @NotNull
    private RoleName role;
}