package com.example.auth.dtos.users;

import com.example.auth.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import java.time.Instant;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username; // login name
    private UserRole role;
    private Instant deletedAt;
}
