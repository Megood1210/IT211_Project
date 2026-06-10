package com.rikkeibank.repository;

import com.rikkeibank.dto.response.AccountResponse;
import com.rikkeibank.entity.Account;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountNumber(String accountNumber);
    boolean existsByAccountNumber(String accountNumber);
    Optional<Account> findByUserId(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT a
            FROM Account a
            WHERE a.id = :id
            """)
    Optional<Account> findByIdForUpdate(@Param("id") Long id);

    @Query("""
            SELECT new com.rikkeibank.dto.response.AccountResponse(
                a.id,
                a.accountNumber,
                a.balance,
                a.active,
                u.id,
                u.username
            )
            FROM Account a
            JOIN a.user u
            """)
    Page<AccountResponse> getAccounts(Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT a
            FROM Account a
            WHERE a.accountNumber
            = :accountNumber
            """)
    Optional<Account> findByAccountNumberForUpdate(@Param("accountNumber") String accountNumber);


}