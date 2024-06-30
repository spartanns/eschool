package com.example.server.user.parent;

import com.example.server.user.User;
import com.example.server.user.student.Student;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity @NoArgsConstructor @AllArgsConstructor @Data @Builder
public class Parent {
    private @Id @GeneratedValue(strategy = GenerationType.AUTO) Long id;
    private @Column(nullable = false) String name;
    private @Column(nullable = false) String surname;
    private @Column(nullable = false, unique = true) String email;
    private @OneToOne User user;
    private @JsonManagedReference @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH }) List<Student> students;
}
