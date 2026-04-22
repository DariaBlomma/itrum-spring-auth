package com.example.auth.controllers;

import com.example.auth.dtos.users.UserResponse;
import com.example.auth.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
}
