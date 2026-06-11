package com.rikkeibank.service.impl;

import com.rikkeibank.dto.request.CreateUserRequest;
import com.rikkeibank.dto.request.UpdateUserRequest;
import com.rikkeibank.dto.response.UserResponse;
import com.rikkeibank.entity.Role;
import com.rikkeibank.entity.User;
import com.rikkeibank.exception.DuplicateResourceException;
import com.rikkeibank.exception.ResourceNotFoundException;
import com.rikkeibank.repository.RoleRepository;
import com.rikkeibank.repository.UserRepository;
import com.rikkeibank.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("User not found"));

        return UserResponse.builder().id(user.getId()).username(user.getUsername())
                .email(user.getEmail()).phoneNumber(user.getPhoneNumber()).role(user.getRole()
                        .getName()).isActive(user.getIsActive()).build();
    }

    @Override
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        Role role = roleRepository.findByName(request.getRole()).orElseThrow(() ->
                new ResourceNotFoundException("Role not found"));

        User user = User.builder().username(request.getUsername()).password(passwordEncoder
                .encode(request.getPassword())).email(request.getEmail()).phoneNumber(request
                .getPhoneNumber()).role(role).isActive(true).build();

        userRepository.save(user);

        return UserResponse.builder().id(user.getId()).username(user.getUsername()).email(user
                .getEmail()).phoneNumber(user.getPhoneNumber()).role(role.getName()).isActive(user
                .getIsActive()).build();
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