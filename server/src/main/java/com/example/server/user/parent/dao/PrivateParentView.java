package com.example.server.user.parent.dao;

import com.example.server.user.student.dao.PrivateStudentView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor @AllArgsConstructor @Data @Builder
public class PrivateParentView {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private List<PrivateStudentView> students;
}
