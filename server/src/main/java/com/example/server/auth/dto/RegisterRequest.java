package com.example.server.auth.dto;

import com.example.server.user.Role;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor @AllArgsConstructor @Data @Builder
public class RegisterRequest {

    @NotNull(message = "Username must be provided.")
    @Size(min = 5, max = 25, message = "Username must be between {min} and {max} characters long.")
    private String username;

    @NotNull(message = "Password must be provided.")
    @Size(min = 5, message = "Password must be at least {min} characters long.")
    private String password;

    @NotNull(message = "User must have a role.")
    private Role role;
}
