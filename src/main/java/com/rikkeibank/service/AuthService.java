package com.rikkeibank.service;

import com.rikkeibank.dto.request.LoginRequest;
import com.rikkeibank.dto.request.RefreshTokenRequest;
import com.rikkeibank.dto.request.RegisterRequest;
import com.rikkeibank.dto.response.LoginResponse;
import com.rikkeibank.dto.response.RegisterResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);

    LoginResponse refreshToken(RefreshTokenRequest request);

    void logout(String accessToken);
    RegisterResponse register(RegisterRequest request);
}