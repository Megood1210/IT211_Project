package com.rikkeibank.service.impl;

import com.rikkeibank.dto.request.*;
import com.rikkeibank.entity.*;
import com.rikkeibank.exception.*;
import com.rikkeibank.repository.*;
import com.rikkeibank.service.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void changeTransactionPin(String username, ChangePinRequest request) {
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new ResourceNotFoundException("User not found"));

        Account account = accountRepository.findByUserId(user.getId()).orElseThrow(() ->
                new ResourceNotFoundException("Account not found"));

        boolean matched = passwordEncoder.matches(request.getOldPin(), account.getTransactionPin());

        if (!matched) {
            throw new InvalidPinException("Old pin incorrect");
        }

        account.setTransactionPin(passwordEncoder.encode(request.getNewPin()));

        accountRepository.save(account);
    }

    @Override
    public String forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow(() ->
                new ResourceNotFoundException("User not found"));

        String token = UUID.randomUUID().toString();

        user.setResetPasswordToken(token);

        user.setResetPasswordExpiredAt(LocalDateTime.now().plusMinutes(15));

        userRepository.save(user);

        return token;
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByResetPasswordToken(request.getToken()).orElseThrow(() ->
                new InvalidResetTokenException("Invalid token"));

        if (user.getResetPasswordExpiredAt().isBefore(LocalDateTime.now())) {
            throw new InvalidResetTokenException("Token expired");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        user.setResetPasswordToken(null);

        user.setResetPasswordExpiredAt(null);

        userRepository.save(user);
    }
}