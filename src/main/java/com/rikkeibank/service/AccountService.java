package com.rikkeibank.service;

import com.rikkeibank.dto.request.CreateAccountRequest;
import com.rikkeibank.dto.response.*;

import org.springframework.data.domain.Page;

public interface AccountService {
    AccountResponse createAccount(CreateAccountRequest request);

    Page<AccountResponse> getAccounts(int page, int size);

    BalanceResponse getMyBalance(String username);
}