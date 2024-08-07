package com.example.server.user.parent.dao;

import com.example.server.security.Views;
import com.example.server.user.student.dao.ParentStudentView;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor @AllArgsConstructor @Data @Builder
public class PrivateParentView {
    private @JsonView(Views.Public.class) Long id;
    private @JsonView(Views.Public.class) String name;
    private @JsonView(Views.Public.class) String surname;
    private @JsonView(Views.Public.class) String email;
    private @JsonView(Views.Public.class) List<ParentStudentView> students;
}
