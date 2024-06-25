package com.example.server.user.teacher.dto;

import com.example.server.auth.dto.RegisterRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor @AllArgsConstructor @Data
public class TeacherRequest extends RegisterRequest {

    @NotNull(message = "Teacher must have a name.")
    @Size(min = 3, max = 25, message = "Name must be between {min} and {max} characters long.")
    private String name;

    @NotNull(message = "Teacher must have a surname.")
    @Size(min = 3, max = 25, message = "Surname must be between {min} and {max} characters long.")
    private String surname;
}
