package com.rikkeibank.service.impl;

import com.rikkeibank.dto.request.TransferRequest;
import com.rikkeibank.dto.response.StatementResponse;
import com.rikkeibank.dto.response.TransactionResponse;
import com.rikkeibank.entity.Account;
import com.rikkeibank.entity.Transaction;
import com.rikkeibank.entity.User;
import com.rikkeibank.enums.TransactionStatus;
import com.rikkeibank.exception.*;
import com.rikkeibank.repository.*;
import com.rikkeibank.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public TransactionResponse transfer(String username, TransferRequest request) {
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new ResourceNotFoundException("User not found"));

        Account sourceAccount = accountRepository.findByUserId(user.getId()).orElseThrow(() ->
                new ResourceNotFoundException("Source account not found"));

        Account lockedSource = accountRepository.findByAccountNumberForUpdate(sourceAccount
                .getAccountNumber()).orElseThrow();

        Account lockedTarget = accountRepository.findByAccountNumberForUpdate(request
                .getTargetAccountNumber()).orElseThrow(() ->
                new ResourceNotFoundException("Target account not found"));

        if (!passwordEncoder.matches(request.getTransactionPin(), lockedSource.getTransactionPin())) {

            throw new InvalidPinException("Invalid PIN");
        }

        if (lockedSource.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        lockedSource.setBalance(lockedSource.getBalance().subtract(request.getAmount()));

        lockedTarget.setBalance(lockedTarget.getBalance().add(request.getAmount()));

        accountRepository.save(lockedSource);

        accountRepository.save(lockedTarget);

        Transaction transaction = Transaction.builder().transactionCode(UUID.randomUUID()
                .toString()).amount(request.getAmount()).description(request.getDescription())
                .status(TransactionStatus.SUCCESS).createdAt(LocalDateTime.now())
                .fromAccount(lockedSource).toAccount(lockedTarget).build();

        transactionRepository.save(transaction);

        return TransactionResponse.builder().id(transaction.getId()).transactionCode(transaction
                .getTransactionCode()).fromAccount(lockedSource.getAccountNumber())
                .toAccount(lockedTarget.getAccountNumber()).amount(transaction.getAmount())
                .description(transaction.getDescription()).status(transaction.getStatus().name())
                .createdAt(transaction.getCreatedAt()).build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StatementResponse> getMyStatement(String username, int page, int size) {
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new ResourceNotFoundException("User not found"));

        Account account = accountRepository.findByUserId(user.getId()).orElseThrow(() ->
                new ResourceNotFoundException("Account not found"));

        Pageable pageable = PageRequest.of(page, size);

        Page<Transaction> transactions = transactionRepository
                .getTransactionHistory(account.getId(), pageable);

        return transactions.map(transaction -> {

            boolean isDebit = transaction.getFromAccount().getId().equals(account.getId());

            return StatementResponse.builder().transactionId(transaction
                    .getId()).transactionCode(transaction.getTransactionCode())
                    .type(isDebit ? "DEBIT" : "CREDIT").amount(transaction.getAmount())
                    .description(transaction.getDescription()).status(transaction.getStatus()
                            .name()).counterPartyAccount(isDebit ? transaction.getToAccount()
                            .getAccountNumber() : transaction.getFromAccount().getAccountNumber())
                    .createdAt(transaction.getCreatedAt()).build();
        });
    }
}