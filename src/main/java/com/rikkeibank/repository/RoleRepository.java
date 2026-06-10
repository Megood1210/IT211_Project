package com.rikkeibank.repository;

import com.rikkeibank.entity.Role;
import com.rikkeibank.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}