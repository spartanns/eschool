package com.example.server.user.student;

import com.example.server.admin.department.Department;
import com.example.server.management.grade.Grade;
import com.example.server.security.Views;
import com.example.server.user.User;
import com.example.server.user.parent.Parent;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity @NoArgsConstructor @AllArgsConstructor @Data @Builder
public class Student {
    private @Id @GeneratedValue(strategy = GenerationType.AUTO) @JsonView(Views.Public.class) Long id;
    private @Column(nullable = false) @JsonView(Views.Public.class) String name;
    private @Column(nullable = false) @JsonView(Views.Public.class) String surname;
    private @OneToOne @JsonView(Views.Admin.class) User user;
    private @JsonBackReference @ManyToOne @JoinColumn(name = "parent") Parent parent;
    @JsonView(Views.Private.class)
    private @JsonBackReference @ManyToOne @JoinColumn(name = "department") Department dept;
    @JsonView(Views.Private.class)
    private @JsonManagedReference @OneToMany(mappedBy = "student", fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH }) List<Grade> grades;
    // TODO: private List<Comment> comments;
    private @JsonView(Views.Private.class) int rating = 5;
    private @JsonView(Views.Private.class) int attended = 0;
    private @JsonView(Views.Private.class) int unattended = 0;
}
