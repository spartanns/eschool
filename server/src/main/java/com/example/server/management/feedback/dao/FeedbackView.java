package com.example.server.management.feedback.dao;

import com.example.server.management.feedback.FeedbackType;
import com.example.server.security.Views;
import com.example.server.user.teacher.dao.GradeTeacherView;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor @AllArgsConstructor @Data @Builder
public class FeedbackView {
    private @JsonView(Views.Public.class) Long id;
    private @JsonView(Views.Public.class) FeedbackType type;
    private @JsonView(Views.General.class) String text;
    private @JsonView(Views.Private.class) GradeTeacherView createdBy;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private @JsonView(Views.Private.class) Date createdAt;
}
