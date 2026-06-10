package com.rikkeibank.controller;

import com.rikkeibank.dto.request.*;
import com.rikkeibank.service.SecurityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class SecurityController {
    private final SecurityService securityService;

    @PutMapping("/customer/accounts/change-pin")
    public Map<String, String> changePin(Authentication authentication,
                                         @RequestBody @Valid ChangePinRequest request) {
        securityService.changeTransactionPin(authentication.getName(), request);

        return Map.of("message", "PIN changed successfully");
    }

    @PostMapping("/auth/forgot-password")
    public Map<String, String> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        String token = securityService.forgotPassword(request);

        return Map.of("resetToken", token);
    }

    @PostMapping("/auth/reset-password")
    public Map<String, String> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        securityService.resetPassword(request);

        return Map.of("message", "Password reset success");
    }
}