package com.example.server.management.lecture.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor @AllArgsConstructor @Data
public class LectureRequest {

    @NotNull(message = "Subject ID must be provided.")
    private Long subjectID;

    @NotNull(message = "Dept ID must be provided.")
    private Long deptID;

    @NotNull(message = "Teacher ID must be provided.")
    private Long teacherID;
}
