package com.example.server.user.teacher;

import com.example.server.admin.department.Department;
import com.example.server.management.feedback.Feedback;
import com.example.server.management.grade.Grade;
import com.example.server.management.lecture.Lecture;
import com.example.server.user.User;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity @NoArgsConstructor @AllArgsConstructor @Data @Builder
public class Teacher {
    private @Id @GeneratedValue(strategy = GenerationType.AUTO) Long id;
    private @Column(nullable = false) String name;
    private @Column(nullable = false) String surname;
    private @OneToOne User user;
    private @JsonManagedReference @ManyToMany @JoinTable(name = "teacher_dept", joinColumns = @JoinColumn(name = "teacher_id"), inverseJoinColumns = @JoinColumn(name = "dept_id")) List<Department> departments;
    private @JsonManagedReference @OneToMany(mappedBy = "teacher", fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH }) List<Lecture> lectures;
    private @JsonManagedReference @OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH }) List<Grade> grades;
    private @JsonManagedReference @OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH }) List<Feedback> feedbacks;
}