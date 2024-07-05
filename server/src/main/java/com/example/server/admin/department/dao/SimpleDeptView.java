package com.example.server.admin.department.dao;

import com.example.server.security.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor @AllArgsConstructor @Data @Builder
public class SimpleDeptView {
    private @JsonView(Views.Public.class) Long id;
    private @JsonView(Views.Public.class) String name;
    private @JsonView(Views.Public.class) int teachers;
    private @JsonView(Views.Public.class) int students;
    private @JsonView(Views.Public.class) int subjects;
}
