package com.example.server.management.grade.dao;

import com.example.server.management.grade.Type;
import com.example.server.management.lecture.Lecture;
import com.example.server.management.subject.Semester;
import com.example.server.security.Views;
import com.example.server.user.student.dao.GradeStudentView;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor @AllArgsConstructor @Data @Builder
public class TeacherGradeView {
    private @JsonView(Views.Public.class) Long id;
    private @JsonView(Views.Public.class) int value;
    private @JsonView(Views.Public.class) String subject;
    private @JsonView(Views.General.class) Type type;
    private @JsonView(Views.General.class) Semester semester;
    private @JsonView(Views.General.class) GradeStudentView student;
    private @JsonView(Views.Private.class) Lecture lecture;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private @JsonView(Views.Private.class) Date createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private @JsonView(Views.Private.class) Date updatedAt;
}
