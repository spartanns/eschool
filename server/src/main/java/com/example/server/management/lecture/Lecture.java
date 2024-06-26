package com.example.server.management.lecture;

import com.example.server.admin.department.Department;
import com.example.server.management.subject.Subject;
import com.example.server.security.Views;
import com.example.server.user.teacher.Teacher;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity @NoArgsConstructor @AllArgsConstructor @Data @Builder
public class Lecture {
    private @Id @GeneratedValue(strategy = GenerationType.AUTO) @JsonView(Views.Public.class) Long id;
    private @JsonBackReference @ManyToOne @JoinColumn(name = "subject") Subject subject;
    private @JsonBackReference @ManyToOne @JoinColumn(name = "teacher") Teacher teacher;
    private @JsonManagedReference @ManyToOne @JoinColumn(name = "dept") @JsonIgnore Department dept;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private @Column(nullable = false) @JsonView(Views.Public.class) Date createdAt;
}
