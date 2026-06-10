package com.rikkeibank.service.impl;

import com.rikkeibank.dto.request.*;
import com.rikkeibank.dto.response.UserResponse;
import com.rikkeibank.entity.Role;
import com.rikkeibank.entity.User;
import com.rikkeibank.exception.*;
import com.rikkeibank.repository.RoleRepository;
import com.rikkeibank.repository.UserRepository;
import com.rikkeibank.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Page<UserResponse> getUsers(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return userRepository.getUsers(keyword, pageable);
    }

    @Override
    public UserResponse getUserById(Long id) {
        try {
            System.out.println("STEP 1");

            User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

            System.out.println("STEP 2");

            System.out.println("USERNAME = " + user.getUsername());

            System.out.println("STEP 3");

            System.out.println("ROLE = " + user.getRole());

            System.out.println("STEP 4");

            return UserResponse.builder().id(user.getId()).username(user.getUsername()).email(user.getEmail()).phoneNumber(user.getPhoneNumber()).role(user.getRole().getName()).isActive(user.getIsActive()).build();

        } catch (Exception e) {

            e.printStackTrace();

            throw e;
        }
    }

    @Override
    public UserResponse createUser(CreateUserRequest request) {
        try {
            System.out.println("STEP 1");

            if (userRepository.existsByUsername(request.getUsername())) {
                throw new DuplicateResourceException("Username already exists");
            }

            System.out.println("STEP 2");

            if (userRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("Email already exists");
            }

            System.out.println("STEP 3");

            Role role = roleRepository.findByName(request.getRole()).orElseThrow(() -> new ResourceNotFoundException("Role not found"));

            System.out.println("STEP 4");

            User user = User.builder().username(request.getUsername()).password(passwordEncoder.encode(request.getPassword())).email(request.getEmail()).phoneNumber(request.getPhoneNumber()).role(role).isActive(true).build();

            System.out.println("STEP 5");

            userRepository.save(user);

            System.out.println("STEP 6");

            return UserResponse.builder().id(user.getId()).username(user.getUsername()).email(user.getEmail()).phoneNumber(user.getPhoneNumber()).role(role.getName()).isActive(user.getIsActive()).build();

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("User not found"));

        user.setEmail(request.getEmail());

        user.setPhoneNumber(request.getPhoneNumber());

        if (request.getIsActive() != null) {
            user.setIsActive(request.getIsActive());
        }

        userRepository.save(user);

        return getUserById(id);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("User not found"));

        userRepository.delete(user);
    }
}