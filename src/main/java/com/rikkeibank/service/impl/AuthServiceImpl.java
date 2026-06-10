package com.rikkeibank.service.impl;

import com.rikkeibank.config.JwtProperties;
import com.rikkeibank.dto.request.LoginRequest;
import com.rikkeibank.dto.request.RefreshTokenRequest;
import com.rikkeibank.dto.request.RegisterRequest;
import com.rikkeibank.dto.response.LoginResponse;
import com.rikkeibank.dto.response.RegisterResponse;
import com.rikkeibank.entity.Account;
import com.rikkeibank.entity.RefreshToken;
import com.rikkeibank.entity.TokenBlacklist;
import com.rikkeibank.entity.User;
import com.rikkeibank.enums.RoleName;
import com.rikkeibank.exception.InvalidTokenException;
import com.rikkeibank.repository.*;
import com.rikkeibank.security.jwt.JwtProvider;
import com.rikkeibank.security.principal.CustomUserDetails;
import com.rikkeibank.security.principal.CustomUserDetailsService;
import com.rikkeibank.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtProvider jwtProvider;
    private final JwtProperties jwtProperties;

    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();

        System.out.println("INPUT PASSWORD = " + request.getPassword());

        System.out.println("DB PASSWORD = " + user.getPassword());

        boolean match = passwordEncoder.matches(request.getPassword(), user.getPassword());

        System.out.println("MATCH = " + match);

        try {
            System.out.println("STEP 1");

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            System.out.println("STEP 2");

        } catch (Exception e) {

            e.printStackTrace();

            throw e;
        }

        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(request.getUsername());

        String accessToken = jwtProvider.generateAccessToken(userDetails);

        String refreshToken = jwtProvider.generateRefreshToken(userDetails);

        refreshTokenRepository.save(RefreshToken.builder().token(refreshToken).expiryDate(LocalDateTime.now().plusDays(1)).user(user).build());

        return LoginResponse.builder().accessToken(accessToken).refreshToken(refreshToken).tokenType("Bearer").expiresIn(jwtProperties.getAccessTokenExpiration()).role(user.getRole().getName().name()).build();
    }

    @Override
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken oldToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));

        if (oldToken.getRevoked() || oldToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Refresh token expired");
        }

        User user = oldToken.getUser();

        refreshTokenRepository.delete(oldToken);

        CustomUserDetails userDetails = new CustomUserDetails(user);

        String accessToken = jwtProvider.generateAccessToken(userDetails);

        String refreshToken = jwtProvider.generateRefreshToken(userDetails);

        refreshTokenRepository.save(RefreshToken.builder().token(refreshToken).user(user)
                .expiryDate(LocalDateTime.now().plusDays(1)).build());

        return LoginResponse.builder().accessToken(accessToken).refreshToken(refreshToken)
                .tokenType("Bearer").expiresIn(jwtProperties.getAccessTokenExpiration())
                .role(user.getRole().getName().name()).build();
    }

    @Override
    public void logout(String accessToken) {
        tokenBlacklistRepository.save(TokenBlacklist.builder().accessToken(accessToken)
                .expiryAt(LocalDateTime.now().plusMinutes(5)).build());
    }

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Password mismatch");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        User user = User.builder().username(request.getUsername()).email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())).isActive(true)
                .role(roleRepository.findByName(RoleName.CUSTOMER).orElseThrow()).build();

        userRepository.save(user);

        Account account = Account.builder().accountNumber(generateAccountNumber())
                .transactionPin(passwordEncoder.encode(request.getTransactionPin()))
                .balance(BigDecimal.ZERO).active(true).user(user).build();

        accountRepository.save(account);

        return RegisterResponse.builder().userId(user.getId()).username(user.getUsername())
                .accountNumber(account.getAccountNumber()).balance(account.getBalance())
                .status("ACTIVE").build();
    }

    private String generateAccountNumber() {
        String millis = String.valueOf(System.currentTimeMillis());

        return "10" + millis.substring(5);
    }
}