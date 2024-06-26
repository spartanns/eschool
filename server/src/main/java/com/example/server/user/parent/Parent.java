package com.example.server.user.parent;

import com.example.server.security.Views;
import com.example.server.user.User;
import com.example.server.user.student.Student;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity @NoArgsConstructor @AllArgsConstructor @Data @Builder
public class Parent {
    private @Id @GeneratedValue(strategy = GenerationType.AUTO) @JsonView(Views.Public.class) Long id;
    private @Column(nullable = false) @JsonView(Views.Public.class) String name;
    private @Column(nullable = false) @JsonView(Views.Public.class) String surname;
    private @Column(nullable = false, unique = true) @JsonView(Views.Private.class) String email;
    private @OneToOne @JsonView(Views.Admin.class) User user;
    private @JsonManagedReference @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH }) @JsonView(Views.Private.class) List<Student> students;
}
