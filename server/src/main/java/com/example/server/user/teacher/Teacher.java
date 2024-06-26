package com.example.server.user.teacher;

import com.example.server.admin.department.Department;
import com.example.server.management.grade.Grade;
import com.example.server.management.lecture.Lecture;
import com.example.server.security.Views;
import com.example.server.user.User;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity @NoArgsConstructor @AllArgsConstructor @Data @Builder
public class Teacher {
    private @Id @GeneratedValue(strategy = GenerationType.AUTO) @JsonView(Views.Public.class) Long id;
    private @Column(nullable = false) @JsonView(Views.Public.class) String name;
    private @Column(nullable = false) @JsonView(Views.Public.class) String surname;
    private @OneToOne @JsonView(Views.Admin.class) User user;
    @JsonView(Views.Public.class)
    private @JsonManagedReference @ManyToMany @JoinTable(name = "teacher_dept", joinColumns = @JoinColumn(name = "teacher_id"), inverseJoinColumns = @JoinColumn(name = "dept_id")) List<Department> departments;
    @JsonView(Views.Private.class)
    private @JsonManagedReference @OneToMany(mappedBy = "teacher", fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH }) List<Lecture> lectures;
    @JsonView(Views.Private.class)
    private @JsonManagedReference @OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH }) List<Grade> grades;
}