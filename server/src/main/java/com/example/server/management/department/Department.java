package com.example.server.management.department;

import com.example.server.management.lecture.Lecture;
import com.example.server.management.subject.Subject;
import com.example.server.user.student.Student;
import com.example.server.user.teacher.Teacher;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity @NoArgsConstructor @AllArgsConstructor @Data @Builder
public class Department {
    private @Id @GeneratedValue(strategy = GenerationType.AUTO) Long id;
    private @Column(nullable = false, unique = true) String name;
    private @JsonManagedReference @OneToMany(mappedBy = "dept", fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH }) List<Student> students;
    private @JsonManagedReference @OneToMany(mappedBy = "dept", fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH }) List<Subject> subjects;
    private @JsonManagedReference @OneToMany(mappedBy = "dept", fetch = FetchType.LAZY, cascade = {CascadeType.REFRESH }) List<Lecture> lectures;
    private @JsonBackReference @ManyToMany @JoinTable(name = "dept_teacher", joinColumns = @JoinColumn(name = "dept_id"), inverseJoinColumns = @JoinColumn(name = "teacher_id")) List<Teacher> teachers;
}
