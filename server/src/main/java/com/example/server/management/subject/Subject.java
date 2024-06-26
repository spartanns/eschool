package com.example.server.management.subject;

import com.example.server.admin.department.Department;
import com.example.server.management.grade.Grade;
import com.example.server.management.lecture.Lecture;
import com.example.server.security.Views;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity @NoArgsConstructor @AllArgsConstructor @Data @Builder
public class Subject {
    private @Id @GeneratedValue(strategy = GenerationType.AUTO) @JsonView(Views.Public.class) Long id;
    private @Column(nullable = false) @JsonView(Views.Public.class) String name;
    private @Enumerated @Column(nullable = false) @JsonView(Views.Public.class) Semester semester;
    private @JsonManagedReference @OneToMany(mappedBy = "subject", fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH }) @JsonView(Views.Public.class) List<Lecture> lectures;
    private @JsonBackReference @OneToMany(mappedBy = "subject", fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH }) List<Grade> grades;
    private @JsonBackReference @ManyToOne @JoinColumn(name = "dept") Department dept;
    private @JsonView(Views.Public.class) int hours = 0;
}
