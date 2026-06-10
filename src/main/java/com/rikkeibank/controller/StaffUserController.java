package com.rikkeibank.controller;

import com.rikkeibank.dto.response.UserResponse;
import com.rikkeibank.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/staff/users")
@RequiredArgsConstructor
public class StaffUserController {
    private final UserService userService;

    @GetMapping
    public Page<UserResponse> getUsers(@RequestParam(required = false) String keyword,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        return userService.getUsers(keyword, page, size);
    }
}