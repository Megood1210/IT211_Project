package com.rikkeibank.controller;

import com.rikkeibank.dto.response.BalanceResponse;
import com.rikkeibank.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customer/accounts")
@RequiredArgsConstructor
public class CustomerAccountController {
    private final AccountService accountService;

    @GetMapping("/balance")
    public BalanceResponse getBalance(Authentication authentication) {
        return accountService.getMyBalance(authentication.getName());
    }
}