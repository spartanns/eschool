package com.example.server.user.teacher.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor @AllArgsConstructor @Data @Builder
public class TeacherUpdateRequest {

    @NotNull(message = "Name must be provided.")
    @Size(min = 2, max = 25, message = "Name must be between {min} and {max} characters long.")
    private String name;

    @NotNull(message = "Surname must be provided.")
    @Size(min = 2, max = 25, message = "Surname must be between {min} and {max} characters long.")
    private String surname;
}
