package com.example.server.admin.department.dao;

import com.example.server.management.subject.dao.SingleSubjectView;
import com.example.server.security.Views;
import com.example.server.user.student.dao.AdminStudentView;
import com.example.server.user.teacher.dao.AdminTeacherView;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor @AllArgsConstructor @Data @Builder
public class AdminDeptView {
    private @JsonView(Views.Public.class) Long id;
    private @JsonView(Views.Public.class) String name;
    private @JsonView(Views.General.class) List<SingleSubjectView> subjects;
    private @JsonView(Views.General.class) List<AdminTeacherView> teachers;
    private @JsonView(Views.General.class) List<AdminStudentView> students;
}
