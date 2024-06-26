package com.example.server.management.grade;

import com.example.server.management.lecture.Lecture;
import com.example.server.management.subject.Subject;
import com.example.server.user.User;
import com.example.server.user.student.Student;
import com.example.server.user.teacher.Teacher;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity @NoArgsConstructor @AllArgsConstructor @Data @Builder
public class Grade {
    private @Id @GeneratedValue(strategy = GenerationType.AUTO) Long id;
    private @Column(nullable = false) int value;
    private @Enumerated @Column(nullable = false) Type type;
    private @OneToOne Subject subject;
    private @JsonBackReference @ManyToOne @JoinColumn(name = "lecture") Lecture lecture;
    private @JsonBackReference @ManyToOne @JoinColumn(name = "student") Student student;
    private @JsonBackReference @ManyToOne @JoinColumn(name = "createdBy") Teacher createdBy;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private @Column(nullable = false, updatable = false) Date createdAt;
    private @JsonBackReference @ManyToOne @JoinColumn(name = "updatedBy") User updatedBy;
    private @Column(insertable = false) Date updatedAt;
}
