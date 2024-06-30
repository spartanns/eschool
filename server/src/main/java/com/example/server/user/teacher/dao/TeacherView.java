package com.example.server.user.teacher.dao;

import com.example.server.admin.department.Department;
import com.example.server.admin.department.dao.SingleDeptView;
import com.example.server.management.feedback.dao.TeacherFeedbackView;
import com.example.server.management.grade.dao.TeacherGradeView;
import com.example.server.management.lecture.dao.LectureView;
import com.example.server.security.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor @AllArgsConstructor @Data @Builder
public class TeacherView {
    private @JsonView(Views.Public.class) Long id;
    private @JsonView(Views.Public.class) String name;
    private @JsonView(Views.Public.class) String surname;
    private @JsonView(Views.Public.class) List<SingleDeptView> departments;
    private @JsonView(Views.Public.class) List<TeacherGradeView> grades;
    private @JsonView(Views.Public.class) List<LectureView> lectures;
    private @JsonView(Views.Public.class) List<TeacherFeedbackView> feedbacks;
}
