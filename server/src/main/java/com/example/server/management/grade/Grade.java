package com.example.server.management.grade;

import com.example.server.management.lecture.Lecture;
import com.example.server.management.subject.Subject;
import com.example.server.security.Views;
import com.example.server.user.User;
import com.example.server.user.student.Student;
import com.example.server.user.teacher.Teacher;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity @NoArgsConstructor @AllArgsConstructor @Data @Builder
public class Grade {
    private @Id @GeneratedValue(strategy = GenerationType.AUTO) @JsonView(Views.Public.class) Long id;
    private @Column(nullable = false) @JsonView(Views.Private.class) int value;
    private @Enumerated @Column(nullable = false) @JsonView(Views.Private.class) Type type;
    private @JsonManagedReference @ManyToOne @JoinColumn(name = "subject") @JsonView(Views.Public.class) Subject subject;
    private @JsonBackReference @ManyToOne @JoinColumn(name = "lecture") Lecture lecture;
    private @JsonBackReference @ManyToOne @JoinColumn(name = "student") Student student;
    private @JsonBackReference @ManyToOne @JoinColumn(name = "createdBy") Teacher createdBy;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private @Column(nullable = false, updatable = false) @JsonView(Views.Private.class) Date createdAt;
    private @JsonBackReference @ManyToOne @JoinColumn(name = "updatedBy") User updatedBy;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private @Column(insertable = false) @JsonView(Views.Private.class) Date updatedAt;
}
