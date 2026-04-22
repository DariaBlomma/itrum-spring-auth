package com.example.auth.services;

import com.example.auth.dtos.auth.SignUpRequest;
import com.example.auth.dtos.users.UserResponse;
import com.example.auth.dtos.users.UserResponseWithPassword;
import com.example.auth.entities.User;
import com.example.auth.exceptions.ConflictException;
import com.example.auth.exceptions.ResourceNotFoundException;
import com.example.auth.mappers.UserMapper;
import com.example.auth.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse create(SignUpRequest request) {
        if (userRepository.existsByUsernameAndDeletedAtIsNull(request.getUsername())) {
            throw new ConflictException("User with such name already exists");
        }

        User userForSaving = userMapper.toEntityWithoutPassword(request);
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        userForSaving.setPassword(encodedPassword);

        User savedUser = userRepository.save(userForSaving);
        return userMapper.toResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponse getMe(Long userId) {
        User user = userRepository.findActiveById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User does not exist or deleted with such id " + userId)
        );
        return userMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponseWithPassword getMeWithPassword(String username) {
        User user  = userRepository
                .findActiveByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User does not exist or deleted with username: " + username));
        return userMapper.toResponseWithPassword(user);
    }
}
