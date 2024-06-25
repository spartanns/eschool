package com.example.server.management.subject.dto;

import com.example.server.management.subject.Semester;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor @AllArgsConstructor @Data @Builder
public class SubjectRequest {

    @NotNull(message = "Subject must have a name.")
    @Size(min = 4, max = 20, message = "Subject name must be between {min} and {max} characters long.")
    private String name;

    @NotNull(message = "Subject must be assigned to a semester.")
    private Semester semester;
}
