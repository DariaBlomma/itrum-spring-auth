package com.example.auth.services;

import com.example.auth.dtos.auth.AuthResponse;
import com.example.auth.dtos.auth.LoginRequest;
import com.example.auth.dtos.auth.SignUpRequest;
import com.example.auth.dtos.users.UserResponse;
import com.example.auth.dtos.users.UserResponseWithPassword;
import com.example.auth.exceptions.UnauthorizedException;
import com.example.auth.mappers.UserMapper;
import com.example.auth.security.JwtService;
import lombok.RequiredArgsConstructor;
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

    @Transactional
    public AuthResponse signUp(SignUpRequest request) {
        UserResponse userResponse = userService.create(request);
        return buildAuthResponse(userResponse);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        UserResponseWithPassword user = userService.getMeWithPassword(request.getUsername());
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid username or password");
        }

        return buildAuthResponse(userMapper.toResponseWithPassword(user));
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