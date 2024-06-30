package com.example.server.management.feedback;

import com.example.server.management.lecture.Lecture;
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
public class Feedback {
    private @Id @GeneratedValue(strategy = GenerationType.AUTO) Long id;
    private @Column(nullable = false) FeedbackType type;
    private @Column(nullable = false) String text;
    private @JsonBackReference @ManyToOne @JoinColumn(name = "student") Student student;
    private @JsonBackReference @ManyToOne @JoinColumn(name = "lecture") Lecture lecture;
    private @JsonBackReference @ManyToOne @JoinColumn(name = "createdBy") Teacher createdBy;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private @Column(nullable = false, updatable = false) Date createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private @Column(insertable = false) Date updatedAt;
}
