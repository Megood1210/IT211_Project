package com.rikkeibank.service;

import com.rikkeibank.dto.request.CreateUserRequest;
import com.rikkeibank.dto.request.UpdateUserRequest;
import com.rikkeibank.dto.response.UserResponse;
import org.springframework.data.domain.Page;

public interface UserService {
    Page<UserResponse> getUsers(String keyword, int page, int size);

    UserResponse getUserById(Long id);

    UserResponse createUser(CreateUserRequest request);

    UserResponse updateUser(Long id, UpdateUserRequest request);

    void deleteUser(Long id);
}