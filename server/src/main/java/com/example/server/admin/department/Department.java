package com.example.server.admin.department;

import com.example.server.management.lecture.Lecture;
import com.example.server.management.subject.Subject;
import com.example.server.security.Views;
import com.example.server.user.student.Student;
import com.example.server.user.teacher.Teacher;
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
public class Department {
    private @Id @GeneratedValue(strategy = GenerationType.AUTO) @JsonView(Views.Public.class) Long id;
    private @Column(nullable = false, unique = true) @JsonView(Views.Public.class) String name;
    @JsonView(Views.Private.class)
    private @JsonManagedReference @OneToMany(mappedBy = "dept", fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH }) List<Student> students;
    @JsonView(Views.Public.class)
    private @JsonManagedReference @OneToMany(mappedBy = "dept", fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH }) List<Subject> subjects;
    private @JsonBackReference @OneToMany(mappedBy = "dept", fetch = FetchType.LAZY, cascade = {CascadeType.REFRESH }) List<Lecture> lectures;
    private @JsonBackReference @ManyToMany @JoinTable(name = "dept_teacher", joinColumns = @JoinColumn(name = "dept_id"), inverseJoinColumns = @JoinColumn(name = "teacher_id")) List<Teacher> teachers;
}