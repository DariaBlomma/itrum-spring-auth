package com.example.auth.services;

import com.example.auth.entities.User;
import com.example.auth.exceptions.ResourceNotFoundException;
import com.example.auth.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginAttemptService {
    private final UserRepository userRepository;
    private static final int MAX_FAILED_ATTEMPTS = 5;

    @Transactional
    public void loginSucceeded(String username) {
        User user = userRepository.findActiveByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found or deleted: " + username));
        user.setFailedAttempts(0);
        log.info("LOGIN SUCCESS: User '{}' logged in successfully", username);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void loginFailed(String username) {
        User user = userRepository.findActiveByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found or deleted: " + username));

        int newAttempts = user.getFailedAttempts() + 1;
        user.setFailedAttempts(newAttempts);
        log.warn("LOGIN FAILED: Failed attempt for user '{}' (attempts: {})", username, user.getFailedAttempts());

        if (newAttempts >= MAX_FAILED_ATTEMPTS) {
            user.setAccountNonLocked(false);
            log.error("ACCOUNT LOCKED: User '{}' locked after {} failed attempts", username, MAX_FAILED_ATTEMPTS);
        }
    }


    @Transactional
    public void unlockAccount(Long userId) {
        User user = userRepository.findActiveById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found or deleted: " + userId));
        user.setAccountNonLocked(true);
        user.setFailedAttempts(0);
        log.info("ACCOUNT UNLOCKED: Admin unlocked user '{}'", userId);
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