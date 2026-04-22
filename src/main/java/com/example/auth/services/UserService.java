package com.example.auth.services;

import com.example.auth.dtos.users.UserResponse;
import com.example.auth.entities.User;
import com.example.auth.exceptions.ResourceNotFoundException;
import com.example.auth.mappers.UserMapper;
import com.example.auth.repositories.UserRepository;
import com.example.auth.repositories.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BookRepository bookRepository;

//    @Transactional(readOnly = true)
//    public UserResponse getMe(Long userId) {
//        User user = userRepository.findActiveById(userId).orElseThrow(
//                () -> new ResourceNotFoundException("User does not exist or deleted with such id " + userId)
//        );
//        return userMapper.toResponse(user);
//    }

    @Transactional(readOnly = true)
    public String getMe(Long userId) {

        return "user found with id " + userId;
    }
}
