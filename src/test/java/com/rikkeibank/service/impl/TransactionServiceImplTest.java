package com.rikkeibank.service.impl;

import com.rikkeibank.dto.request.TransferRequest;
import com.rikkeibank.entity.Account;
import com.rikkeibank.entity.Role;
import com.rikkeibank.entity.User;
import com.rikkeibank.enums.RoleName;
import com.rikkeibank.exception.InsufficientBalanceException;
import com.rikkeibank.repository.AccountRepository;
import com.rikkeibank.repository.TransactionRepository;
import com.rikkeibank.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class TransactionServiceImplTest {
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private User sender;

    private Account fromAccount;

    private Account toAccount;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        Role role = Role.builder().id(1L).name(RoleName.CUSTOMER).build();

        sender = User.builder().id(1L).username("vinh").role(role).build();

        fromAccount = Account.builder().id(1L).accountNumber("111111").balance(BigDecimal
                .valueOf(1_000_000)).transactionPin("encoded-pin").active(true).user(sender).build();

        toAccount = Account.builder().id(2L).accountNumber("999999").balance(BigDecimal.ZERO)
                .transactionPin("encoded-pin").active(true).build();
    }

    @Test
    void transfer_success() {
        TransferRequest request = new TransferRequest();

        request.setAmount(BigDecimal.valueOf(100000));

        request.setTargetAccountNumber("999999");

        request.setTransactionPin("123456");

        when(userRepository.findByUsername("vinh")).thenReturn(Optional.of(sender));

        when(accountRepository.findByUserId(1L)).thenReturn(Optional.of(fromAccount));

        when(accountRepository.findByAccountNumberForUpdate("999999")).thenReturn(Optional
                .of(toAccount));

        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        transactionService.transfer("vinh", request);

        assertEquals(BigDecimal.valueOf(900000), fromAccount.getBalance());

        assertEquals(BigDecimal.valueOf(100000), toAccount.getBalance());
    }

    @Test
    void transfer_insufficient_balance() {
        TransferRequest request = new TransferRequest();

        request.setAmount(BigDecimal.valueOf(99_999_999));

        request.setTargetAccountNumber("999999");

        request.setTransactionPin("123456");

        when(userRepository.findByUsername("vinh")).thenReturn(Optional.of(sender));

        when(accountRepository.findByUserId(1L)).thenReturn(Optional.of(fromAccount));

        assertThrows(InsufficientBalanceException.class,
                () -> transactionService.transfer("vinh", request));
    }
}