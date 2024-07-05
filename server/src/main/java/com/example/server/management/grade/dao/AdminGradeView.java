package com.example.server.management.grade.dao;

import com.example.server.management.grade.Type;
import com.example.server.management.subject.dao.LectureSubjectView;
import com.example.server.security.Views;
import com.example.server.user.student.dao.AdminStudentView;
import com.example.server.user.teacher.dao.AdminTeacherView;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor @AllArgsConstructor @Data @Builder
public class AdminGradeView {
    private @JsonView(Views.Public.class) Long id;
    private @JsonView(Views.Public.class) int value;
    private @JsonView(Views.Public.class) Type type;
    private @JsonView(Views.Public.class) LectureSubjectView subject;
    private @JsonView(Views.General.class) AdminStudentView student;
    private @JsonView(Views.General.class) AdminTeacherView createdBy;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private @JsonView(Views.Public.class) Date createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private @JsonView(Views.Public.class) Date updatedAt;
}
