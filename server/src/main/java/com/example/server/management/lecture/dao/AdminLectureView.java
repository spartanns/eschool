package com.example.server.management.lecture.dao;

import com.example.server.admin.department.dao.AdminDeptView;
import com.example.server.management.feedback.Feedback;
import com.example.server.management.grade.dao.AdminGradeView;
import com.example.server.management.subject.dao.AdminSubjectView;
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
import java.util.List;

@NoArgsConstructor @AllArgsConstructor @Data @Builder
public class AdminLectureView {
    private @JsonView(Views.Public.class) Long id;
    private @JsonView(Views.Public.class) AdminSubjectView subject;
    private @JsonView(Views.Public.class) AdminTeacherView teacher;
    private @JsonView(Views.Private.class) AdminDeptView department;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private @JsonView(Views.Public.class) Date createdAt;
    private @JsonView(Views.General.class) List<AdminStudentView> attendants;
    private @JsonView(Views.General.class) List<AdminGradeView> grades;
    private @JsonView(Views.General.class) List<Feedback> feedbacks;
}
