package com.rikkeibank.service.impl;

import com.rikkeibank.dto.request.CreateAccountRequest;
import com.rikkeibank.dto.response.*;
import com.rikkeibank.entity.Account;
import com.rikkeibank.entity.User;
import com.rikkeibank.exception.ResourceNotFoundException;
import com.rikkeibank.repository.AccountRepository;
import com.rikkeibank.repository.UserRepository;
import com.rikkeibank.service.AccountService;
import com.rikkeibank.util.AccountNumberGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountNumberGenerator generator;

    @Override
    public AccountResponse createAccount(CreateAccountRequest request) {
        User user = userRepository.findById(request.getUserId()).orElseThrow(() ->
                new ResourceNotFoundException("User not found"));

        String accountNumber;

        do {

            accountNumber = generator.generate();

        } while (accountRepository.existsByAccountNumber(accountNumber));

        Account account = Account.builder().accountNumber(accountNumber).balance(request
                .getInitialBalance()).transactionPin(passwordEncoder.encode(request.getPin()))
                .active(true).user(user).build();

        accountRepository.save(account);

        return AccountResponse.builder().id(account.getId()).accountNumber(account.getAccountNumber())
                .balance(account.getBalance()).active(account.getActive()).userId(user.getId())
                .username(user.getUsername()).build();
    }

    @Override
    public Page<AccountResponse> getAccounts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return accountRepository.getAccounts(pageable);
    }

    @Override
    public BalanceResponse getMyBalance(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new ResourceNotFoundException("User not found"));

        Account account = accountRepository.findByUserId(user.getId()).orElseThrow(() ->
                new ResourceNotFoundException("Account not found"));

        return BalanceResponse.builder().accountNumber(account.getAccountNumber()).balance(account
                .getBalance()).currency("VND").build();
    }
}