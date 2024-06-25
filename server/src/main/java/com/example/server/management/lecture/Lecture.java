package com.example.server.management.lecture;

import com.example.server.management.department.Department;
import com.example.server.management.subject.Subject;
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
public class Lecture {
    private @Id @GeneratedValue(strategy = GenerationType.AUTO) Long id;
    private @JsonBackReference @ManyToOne @JoinColumn(name = "subject") Subject subject;
    private @JsonBackReference @ManyToOne @JoinColumn(name = "teacher") Teacher teacher;
    private @JsonBackReference @ManyToOne @JoinColumn(name = "dept") Department dept;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private @Column(nullable = false) Date createdAt;
}
