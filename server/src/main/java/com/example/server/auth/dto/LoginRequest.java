package com.example.server.auth.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@RequiredArgsConstructor @AllArgsConstructor @Data @Builder
public class LoginRequest {

    @NotNull(message = "Username must be provided.")
    @Size(min = 5, max = 25, message = "Username must be between {min} and {max} characters long.")
    private String username;

    @NotNull(message = "Password must be provided.")
    @Size(min = 5, message = "Password must be at least {min} characters long.")
    private String password;
}
