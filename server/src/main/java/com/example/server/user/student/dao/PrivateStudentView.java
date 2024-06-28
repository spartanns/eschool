package com.example.server.user.student.dao;

import com.example.server.management.feedback.dao.FeedbackView;
import com.example.server.management.grade.dao.GradeView;
import com.example.server.security.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor @AllArgsConstructor @Data @Builder
public class PrivateStudentView {
    private @JsonView(Views.Public.class) Long id;
    private @JsonView(Views.Public.class) String name;
    private @JsonView(Views.Public.class) String surname;
    private @JsonView(Views.Public.class) int rating;
    private @JsonView(Views.Public.class) int attended;
    private @JsonView(Views.Public.class) int unattended;
    private @JsonView(Views.Public.class) List<GradeView> grades;
    private @JsonView(Views.Public.class) List<FeedbackView> feedbacks;
}
