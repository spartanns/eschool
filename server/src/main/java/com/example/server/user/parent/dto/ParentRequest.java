package com.example.server.user.parent.dto;

import com.example.server.auth.dto.RegisterRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor @AllArgsConstructor @Data
public class ParentRequest extends RegisterRequest {

    @NotNull(message = "Parent must have a name.")
    @Size(min = 2, max = 20, message = "Parent's name must be between {min} and {max} characters long.")
    private String name;

    @NotNull(message = "Parent must have a surname.")
    @Size(min = 2, max = 20, message = "Parent's surname must be between {min} and {max} characters long.")
    private String surname;

    @NotNull(message = "Parent must have an email.")
    private String email;
}
