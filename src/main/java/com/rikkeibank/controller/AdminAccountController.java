package com.rikkeibank.controller;

import com.rikkeibank.dto.request.CreateAccountRequest;
import com.rikkeibank.dto.response.AccountResponse;
import com.rikkeibank.dto.response.ApiResponse;
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
    public ApiResponse<AccountResponse> create(@RequestBody @Valid CreateAccountRequest request) {
        AccountResponse response = accountService.createAccount(request);
        return ApiResponse.<AccountResponse>builder().status(201)
                .message("Account created successfully").data(response).build();
    }

    @GetMapping
    public ApiResponse<Page<AccountResponse>> getAccounts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Page<AccountResponse> response = accountService.getAccounts(page, size);

        return ApiResponse.<Page<AccountResponse>>builder().status(200)
                .message("Accounts fetched successfully").data(response).build();
    }
}