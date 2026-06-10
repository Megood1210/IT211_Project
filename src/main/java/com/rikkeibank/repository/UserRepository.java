package com.rikkeibank.repository;

import com.rikkeibank.dto.response.UserResponse;
import com.rikkeibank.entity.User;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("""
        SELECT u
        FROM User u
        LEFT JOIN FETCH u.role
        WHERE u.username = :username
        """)
    Optional<User> findByUsername(@Param("username") String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    Boolean existsByPhoneNumber(String phoneNumber);

    @Query("""
            SELECT new com.rikkeibank.dto.response.UserResponse(
                u.id,
                u.username,
                u.email,
                u.phoneNumber,
                r.name,
                u.isActive
            )
            FROM User u
            JOIN u.role r
            WHERE (:keyword IS NULL
                OR LOWER(u.username)
                LIKE LOWER(
                    CONCAT('%', :keyword, '%')
                )
            )
            """)
    Page<UserResponse> getUsers(@Param("keyword") String keyword, Pageable pageable);

    Optional<User> findByResetPasswordToken(String token);


}