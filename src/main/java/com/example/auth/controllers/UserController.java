package com.example.auth.controllers;

import com.example.auth.dtos.users.UserRequest;
import com.example.auth.dtos.users.UserResponse;
import com.example.auth.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/authors")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getMe(@PathVariable("id") Long userId) {
        return userService.getMe(userId);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN)")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(UserRequest request) {
        return userService.create(request);
    }
}
