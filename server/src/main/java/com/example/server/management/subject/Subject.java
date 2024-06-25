package com.example.server.management.subject;

import com.example.server.management.department.Department;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity @NoArgsConstructor @AllArgsConstructor @Data @Builder
public class Subject {
    private @Id @GeneratedValue(strategy = GenerationType.AUTO) Long id;
    private @Column(nullable = false) String name;
    private @Enumerated @Column(nullable = false) Semester semester;
    private @JsonBackReference @ManyToOne @JoinColumn(name = "dept") Department dept;
    private int hours = 0;
}
