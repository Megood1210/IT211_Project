package com.rikkeibank.controller;

import com.rikkeibank.dto.request.LoginRequest;
import com.rikkeibank.dto.request.RefreshTokenRequest;
import com.rikkeibank.dto.response.ApiResponse;
import com.rikkeibank.dto.response.LoginResponse;
import com.rikkeibank.security.LoginRateLimiter;
import com.rikkeibank.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.rikkeibank.dto.request.RegisterRequest;
import com.rikkeibank.dto.response.RegisterResponse;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final
    LoginRateLimiter loginRateLimiter;

    @PostMapping("/register")
    public ApiResponse<RegisterResponse> register(@RequestBody @Valid RegisterRequest request) {
        RegisterResponse response = authService.register(request);

        return ApiResponse.<RegisterResponse>builder().status(201).message("Register success")
                .data(response).build();
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(HttpServletRequest servletRequest,
                                            @RequestBody @Valid LoginRequest request) {
        String ip = servletRequest.getRemoteAddr();

        boolean allowed = loginRateLimiter.allowRequest(ip);

        if (!allowed) {
            throw new RuntimeException("Too many login attempts");
        }

        LoginResponse response = authService.login(request);

        return ApiResponse.<LoginResponse>builder().status(200).message("Login success").data(response).build();
    }

    @PostMapping("/refresh")
    public ApiResponse<LoginResponse> refresh(@RequestBody @Valid RefreshTokenRequest request) {
        LoginResponse response = authService.refreshToken(request);

        return ApiResponse.<LoginResponse>builder().status(200).message("Token refreshed").data(response).build();
    }

    @PostMapping("/logout")
    public ApiResponse<String> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);

        authService.logout(token);

        return ApiResponse.<String>builder().status(200).message("Logout successful").data(null).build();
    }


}