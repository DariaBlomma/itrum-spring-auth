package com.example.auth.controllers;

import com.example.auth.dtos.users.UserRequest;
import com.example.auth.dtos.users.UserResponse;
import com.example.auth.services.LoginAttemptService;
import com.example.auth.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final LoginAttemptService loginAttemptService;

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@Valid @RequestBody UserRequest request) {
        return userService.create(request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getMe(@PathVariable("id") Long userId) {
        return userService.getMe(userId);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("/{id}/unlock")
    @ResponseStatus(HttpStatus.OK)
    public void unlock(@PathVariable("id") Long userId) {
        loginAttemptService.unlockAccount(userId);
    }

}
