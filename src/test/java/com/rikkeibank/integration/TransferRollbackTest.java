package com.rikkeibank.integration;

import com.rikkeibank.dto.request.TransferRequest;
import com.rikkeibank.entity.Account;
import com.rikkeibank.repository.AccountRepository;
import com.rikkeibank.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class TransferRollbackTest {
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void rollback_when_exception() {
        Account sender = accountRepository.findById(1L).orElseThrow();

        BigDecimal before = sender.getBalance();

        TransferRequest request = new TransferRequest();

        request.setAmount(BigDecimal.valueOf(999999999));

        assertThrows(Exception.class,
                () -> transactionService.transfer("customer", request));

        Account after = accountRepository.findById(1L).orElseThrow();

        assertEquals(before, after.getBalance());
    }
}
