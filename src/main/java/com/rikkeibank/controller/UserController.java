package com.rikkeibank.controller;

import com.rikkeibank.dto.request.CreateUserRequest;
import com.rikkeibank.dto.request.UpdateUserRequest;
import com.rikkeibank.dto.response.ApiResponse;
import com.rikkeibank.dto.response.UserResponse;
import com.rikkeibank.entity.Role;
import com.rikkeibank.entity.User;
import com.rikkeibank.exception.DuplicateResourceException;
import com.rikkeibank.exception.ResourceNotFoundException;
import com.rikkeibank.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ApiResponse<Page<UserResponse>> getUsers(@RequestParam(required = false, defaultValue = "")
                                                        String keyword,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size) {
        Page<UserResponse> response = userService.getUsers(keyword, page, size);

        return ApiResponse.<Page<UserResponse>>builder().status(200).message("Users fetched successfully").data(response).build();
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse response = userService.getUserById(id);

        return ApiResponse.<UserResponse>builder().status(200).message("User fetched successfully").data(response).build();
    }

    @PostMapping
    public ApiResponse<UserResponse> createUser(@RequestBody @Valid CreateUserRequest request) {

        UserResponse response = userService.createUser(request);

        return ApiResponse.<UserResponse>builder().status(201).message("User created successfully").data(response).build();
    }

    @PutMapping("/{id}")
    public ApiResponse<UserResponse> updateUser(@PathVariable Long id, @RequestBody @Valid UpdateUserRequest request) {

        UserResponse response = userService.updateUser(id, request);

        return ApiResponse.<UserResponse>builder().status(200).message("User updated successfully").data(response).build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteUser(@PathVariable Long id) {

        userService.deleteUser(id);

        return ApiResponse.<String>builder().status(204).message("User deleted successfully").data(null).build();
    }
}