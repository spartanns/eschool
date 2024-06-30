package com.example.server.user.student;

import com.example.server.admin.department.Department;
import com.example.server.management.feedback.Feedback;
import com.example.server.management.grade.Grade;
import com.example.server.management.lecture.Lecture;
import com.example.server.user.User;
import com.example.server.user.parent.Parent;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity @NoArgsConstructor @AllArgsConstructor @Data @Builder
public class Student {
    private @Id @GeneratedValue(strategy = GenerationType.AUTO) Long id;
    private @Column(nullable = false) String name;
    private @Column(nullable = false) String surname;
    private @OneToOne User user;
    private @JsonBackReference @ManyToOne @JoinColumn(name = "parent") Parent parent;
    private @JsonBackReference @ManyToOne @JoinColumn(name = "department") Department dept;
    private @JsonManagedReference @OneToMany(mappedBy = "student", fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH }) List<Grade> grades;
    private int rating = 5;
    private int attended = 0;
    private int unattended = 0;
    private @JsonManagedReference @OneToMany(mappedBy = "student", fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH }) List<Feedback> feedbacks;
    private @JsonManagedReference @ManyToMany @JoinTable(name = "student_lectures", joinColumns = @JoinColumn(name = "student_id"), inverseJoinColumns = @JoinColumn(name = "lecture_id")) List<Lecture> lectures;
}
