package com.example.server.admin.department.dao;

import com.example.server.management.subject.dao.SingleSubjectView;
import com.example.server.security.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor @AllArgsConstructor @Data @Builder
public class SingleDeptView {
    private @JsonView(Views.Public.class) Long deptID;
    private @JsonView(Views.Public.class) String name;
    private @JsonView(Views.General.class) List<SingleSubjectView> subjects;
}
