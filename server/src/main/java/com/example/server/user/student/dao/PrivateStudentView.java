package com.example.server.user.student.dao;

import com.example.server.management.grade.dao.PrivateGradeView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor @AllArgsConstructor @Data @Builder
public class PrivateStudentView {
    private Long id;
    private String name;
    private String surname;
    private List<PrivateGradeView> grades;
}
