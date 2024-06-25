package com.example.server.user.student.dto;

import com.example.server.auth.dto.RegisterRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor @AllArgsConstructor @Data
public class StudentRequest extends RegisterRequest {

    @NotNull(message = "Student must have a name.")
    @Size(min = 2, max = 20, message = "Student's name must be between {min} and {max} characters long.")
    private String name;

    @NotNull(message = "Student must have a surname.")
    @Size(min = 2, max = 20, message = "Student's surname must be between {min} and {max} characters long.")
    private String surname;
}
