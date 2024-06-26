package com.example.server.management.grade.dao;

import com.example.server.management.grade.Type;
import com.example.server.management.subject.Semester;
import com.example.server.security.Views;
import com.example.server.user.teacher.dao.GradeTeacherView;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor @AllArgsConstructor @Data @Builder
public class GradeView {
    private @JsonView(Views.Public.class) Long gradeID;
    private @JsonView(Views.Public.class) int value;
    private @JsonView(Views.Public.class) String subject;
    private @JsonView(Views.Public.class) Type type;
    private @JsonView(Views.Public.class) Semester semester;
    private @JsonView(Views.Private.class) Date createdAt;
    private @JsonView(Views.Private.class) GradeTeacherView createdBy;
}