package com.example.server.management.feedback;

import com.example.server.management.lecture.Lecture;
import com.example.server.security.Views;
import com.example.server.user.student.Student;
import com.example.server.user.teacher.Teacher;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity @NoArgsConstructor @AllArgsConstructor @Data @Builder
public class Feedback {
    private @Id @GeneratedValue(strategy = GenerationType.AUTO) @JsonView(Views.Public.class) Long id;
    private @Column(nullable = false) @JsonView(Views.Public.class) FeedbackType type;
    private @Column(nullable = false) @JsonView(Views.Public.class) String text;
    private @JsonBackReference @ManyToOne @JoinColumn(name = "student") Student student;
    private @JsonBackReference @ManyToOne @JoinColumn(name = "lecture") Lecture lecture;
    private @JsonBackReference @ManyToOne @JoinColumn(name = "createdBy") Teacher createdBy;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private @Column(nullable = false, updatable = false) @JsonView(Views.Public.class) Date createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private @Column(insertable = false) Date updatedAt;
}
