package com.example.auth.dtos.users;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class UserRequest {
    @NotBlank(message = "User name is required")
    @Size(min = 2, max = 200, message = "User name must be between 2 and 200 characters")
    private String username;

    @NotBlank(message = "User password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}
