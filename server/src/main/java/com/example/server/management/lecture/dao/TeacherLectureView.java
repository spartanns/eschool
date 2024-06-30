package com.example.server.management.lecture.dao;

import com.example.server.management.feedback.dao.TeacherFeedbackView;
import com.example.server.management.grade.dao.TeacherGradeView;
import com.example.server.security.Views;
import com.example.server.user.student.dao.GradeStudentView;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@NoArgsConstructor @AllArgsConstructor @Data @Builder
public class TeacherLectureView {
    private @JsonView(Views.Public.class) Long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private @JsonView(Views.Public.class) Date date;
    private @JsonView(Views.Public.class) String subject;
    private @JsonView(Views.General.class) List<GradeStudentView> attendants;
    private @JsonView(Views.General.class) List<TeacherGradeView> grades;
    private @JsonView(Views.General.class) List<TeacherFeedbackView> feedbacks;
    private @JsonView(Views.Private.class) List<GradeStudentView> students;
}
