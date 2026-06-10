package com.rikkeibank.concurrency;

import com.rikkeibank.dto.request.TransferRequest;
import com.rikkeibank.entity.Account;
import com.rikkeibank.repository.AccountRepository;
import com.rikkeibank.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ConcurrencyTransferTest {
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void no_double_spending() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(10);

        CountDownLatch latch = new CountDownLatch(10);

        for (int i = 0; i < 10; i++) {
            executor.submit(() -> {
                try {
                    TransferRequest request = new TransferRequest();

                    request.setAmount(BigDecimal.valueOf(100000));

                    request.setTargetAccountNumber("999999");

                    request.setTransactionPin("123456");

                    transactionService.transfer("customer", request);

                } catch (Exception ignored) {
                }

                latch.countDown();
            });
        }

        latch.await();

        Account sender = accountRepository.findById(1L).orElseThrow();

        assertTrue(sender.getBalance().compareTo(BigDecimal.ZERO) >= 0);
    }
}
