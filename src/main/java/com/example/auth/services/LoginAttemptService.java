package com.example.auth.services;

import com.example.auth.entities.User;
import com.example.auth.exceptions.ResourceNotFoundException;
import com.example.auth.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginAttemptService {
    private final UserRepository userRepository;
    private static final int MAX_FAILED_ATTEMPTS = 5;

    @Transactional
    public void loginSucceeded(String username) {
        User user = userRepository.findActiveByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found or deleted: " + username));
        user.setFailedAttempts(0);
    }

    @Transactional
    public void loginFailed(String username) {
        User user = userRepository.findActiveByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found or deleted: " + username));

        int newAttempts = user.getFailedAttempts() + 1;
        user.setFailedAttempts(newAttempts);

        if (newAttempts >= MAX_FAILED_ATTEMPTS) {
            user.setAccountNonLocked(false);
        }
    }


    @Transactional
    private void unlockAccount(Long userId) {
        User user = userRepository.findActiveById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found or deleted: " + userId));
        user.setAccountNonLocked(true);
        user.setFailedAttempts(0);
    }

    public boolean isBlocked(String username) {
        User user = userRepository.findActiveByUserName(username)
                .orElse(null);
        if (user == null) {
            return false;
        }

        return !user.isAccountNonLocked();
    }
}