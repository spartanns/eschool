package com.example.server.user.teacher.dao;

import com.example.server.security.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor @AllArgsConstructor @Data @Builder
public class GradeTeacherView {
    private @JsonView(Views.Public.class) Long teacherID;
    private @JsonView(Views.Public.class) String name;
    private @JsonView(Views.Public.class) String surname;
}