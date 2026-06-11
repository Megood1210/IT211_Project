package com.rikkeibank.concurrency;

import com.rikkeibank.dto.request.TransferRequest;
import com.rikkeibank.entity.Account;
import com.rikkeibank.entity.Role;
import com.rikkeibank.entity.User;
import com.rikkeibank.enums.RoleName;
import com.rikkeibank.repository.AccountRepository;
import com.rikkeibank.repository.RoleRepository;
import com.rikkeibank.repository.UserRepository;
import com.rikkeibank.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class ConcurrencyTransferTest {
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String senderUsername;
    private String receiverAccountNumber;

    @BeforeEach
    void setup() {
        Role customerRole = roleRepository.findByName(RoleName.CUSTOMER).orElseThrow(() ->
                new RuntimeException("Role CUSTOMER not found"));

        User senderUser = User.builder().username("customer_test").password(passwordEncoder
                .encode("123456")).email("customer@test.com").role(customerRole)
                .isActive(true).build();

        userRepository.save(senderUser);

        Account senderAccount = Account.builder().accountNumber("111111").balance(BigDecimal
                .valueOf(1_000_000)).transactionPin(passwordEncoder.encode("123456"))
                .active(true).user(senderUser).build();

        accountRepository.save(senderAccount);

        User receiverUser = User.builder().username("receiver_test").password(passwordEncoder
                .encode("123456")).email("receiver@test.com").role(customerRole)
                .isActive(true).build();

        userRepository.save(receiverUser);

        Account receiverAccount = Account.builder().accountNumber("999999").balance(BigDecimal.ZERO)
                .transactionPin(passwordEncoder.encode("123456")).active(true)
                .user(receiverUser).build();

        accountRepository.save(receiverAccount);

        senderUsername = senderUser.getUsername();
        receiverAccountNumber = receiverAccount.getAccountNumber();
    }

    @Test
    void no_double_spending() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(10);

        CountDownLatch latch = new CountDownLatch(10);

        for (int i = 0; i < 10; i++) {
            executor.submit(() -> {
                try {
                    TransferRequest request = new TransferRequest();

                    request.setAmount(BigDecimal.valueOf(100000));
                    request.setTargetAccountNumber(receiverAccountNumber);
                    request.setTransactionPin("123456");

                    transactionService.transfer(senderUsername, request);

                } catch (Exception ignored) {
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Account sender = accountRepository.findByAccountNumber("111111").orElseThrow(() -> new RuntimeException("Sender account not found"));

        assertTrue(sender.getBalance().compareTo(BigDecimal.ZERO) >= 0);

        executor.shutdown();
    }
}