package com.rikkeibank.controller;

import com.rikkeibank.dto.request.CreateAccountRequest;
import com.rikkeibank.dto.response.AccountResponse;
import com.rikkeibank.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/accounts")
@RequiredArgsConstructor
public class AdminAccountController {
    private final AccountService accountService;

    @PostMapping
    public AccountResponse create(@RequestBody @Valid CreateAccountRequest request) {
        return accountService.createAccount(request);
    }

    @GetMapping
    public Page<AccountResponse> getAccounts(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int size) {
        return accountService.getAccounts(page, size);
    }
}