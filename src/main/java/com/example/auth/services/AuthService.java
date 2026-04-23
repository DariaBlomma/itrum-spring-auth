package com.example.auth.services;

import com.example.auth.dtos.auth.AuthResponse;
import com.example.auth.dtos.auth.LoginRequest;
import com.example.auth.dtos.auth.SignUpRequest;
import com.example.auth.dtos.users.UserResponse;
import com.example.auth.dtos.users.UserResponseWithPassword;
import com.example.auth.exceptions.ResourceNotFoundException;
import com.example.auth.exceptions.UnauthorizedException;
import com.example.auth.mappers.UserMapper;
import com.example.auth.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final LoginAttemptService loginAttemptService;

    @Transactional
    public AuthResponse signUp(SignUpRequest request) {
        UserResponse userResponse = userService.create(userMapper.signUpToPlainUserRequest(request));
        return buildAuthResponse(userResponse);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        if (loginAttemptService.isBlocked(request.getUsername())) {
            throw new LockedException("Account is locked");
        }

        String error = "Invalid username or password";
        try {
            UserResponseWithPassword user = userService.getMeWithPassword(request.getUsername());
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                loginAttemptService.loginFailed(request.getUsername());
                throw new UnauthorizedException(error);
            }

            loginAttemptService.loginSucceeded(request.getUsername());
            return buildAuthResponse(userMapper.toResponseWithPassword(user));
        } catch (ResourceNotFoundException e) {
            loginAttemptService.loginFailed(request.getUsername());
            throw new UnauthorizedException(error);
        }
    }

    private AuthResponse buildAuthResponse(UserResponse user) {
        String token = jwtService.generateToken(
                user.getUsername(),
                user.getId()
        );

        return AuthResponse.builder()
                .token(token)
                .expiresIn(jwtService.getExpiresInSeconds())
                .username(user.getUsername())
                .userId(user.getId())
                .build();
    }
}