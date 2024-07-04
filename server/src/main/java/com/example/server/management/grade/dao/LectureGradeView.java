package com.example.server.management.grade.dao;

import com.example.server.management.grade.Type;
import com.example.server.security.Views;
import com.example.server.user.student.dao.AdminStudentView;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor @AllArgsConstructor @Data @Builder
public class LectureGradeView {
    private @JsonView(Views.Public.class) Long id;
    private @JsonView(Views.Public.class) int value;
    private @JsonView(Views.Public.class) Type type;
    private @JsonView(Views.Public.class) AdminStudentView student;
}
