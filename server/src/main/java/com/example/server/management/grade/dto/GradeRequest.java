package com.example.server.management.grade.dto;

import com.example.server.management.grade.Type;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor @AllArgsConstructor @Data
public class GradeRequest {

    @NotNull(message = "Grade must have a value.")
    private int value;

    @NotNull(message = "Grade must have a valid type.")
    private Type type;

    @NotNull(message = "Grade must belong to an existing lecture.")
    private Long lectureID;

    @NotNull(message = "Grade must belong to an existing student.")
    private Long studentID;

    @NotNull(message = "Grade must belong to an existing teacher.")
    private Long teacherID;

}
