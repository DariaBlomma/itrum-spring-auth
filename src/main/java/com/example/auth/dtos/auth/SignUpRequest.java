package com.example.auth.dtos.auth;

import com.example.auth.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SignUpRequest {
    @NotBlank(message = "User name is required")
    @Size(min = 2, max = 200, message = "User name must be between 2 and 200 characters")
    private String username;

    @NotBlank(message = "User password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotNull(message = "Role is required")
    private UserRole role;
}
