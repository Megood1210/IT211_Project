package com.rikkeibank.controller;

import com.rikkeibank.dto.request.TransferRequest;
import com.rikkeibank.dto.response.ApiResponse;
import com.rikkeibank.dto.response.StatementResponse;
import com.rikkeibank.dto.response.TransactionResponse;
import com.rikkeibank.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customer/transactions")
@RequiredArgsConstructor
public class CustomerTransactionController {
    private final TransactionService transactionService;

    @PostMapping("/transfer")
    public ApiResponse<TransactionResponse> transfer(Authentication authentication,
                                                     @RequestBody @Valid TransferRequest request) {
        TransactionResponse response = transactionService.transfer(authentication.getName(), request);

        return ApiResponse.<TransactionResponse>builder().status(200).message("Transfer success")
                .data(response).build();
    }

    @GetMapping("/statement")
    public ApiResponse<Page<StatementResponse>> statement(Authentication authentication,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size) {
        Page<StatementResponse> response = transactionService.getMyStatement(authentication.getName(), page, size);

        return ApiResponse.<Page<StatementResponse>>builder().status(200)
                .message("Get statement success").data(response).build();
    }
}