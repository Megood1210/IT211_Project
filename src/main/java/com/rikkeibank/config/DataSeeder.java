package com.rikkeibank.config;

import com.rikkeibank.entity.Role;
import com.rikkeibank.entity.User;
import com.rikkeibank.enums.RoleName;
import com.rikkeibank.repository.RoleRepository;
import com.rikkeibank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedRoles();
        seedAdmin();
    }

    private void seedRoles() {
        for (RoleName roleName : RoleName.values()) {
            roleRepository.findByName(roleName).orElseGet(() -> roleRepository.save(Role.builder().name(roleName).description(roleName.name()).build()));
        }
    }

    private void seedAdmin() {
        if (userRepository.findByUsername("admin").isPresent()) {
            return;
        }

        Role adminRole = roleRepository.findByName(RoleName.ADMIN).orElseThrow();

        User admin = User.builder().username("admin").password(passwordEncoder.encode("Admin@123")).email("admin@rikkei.com").phoneNumber("0123456789").role(adminRole).build();

        userRepository.save(admin);
    }
}