package com.example.server.management.lecture.dao;

import com.example.server.security.Views;
import com.example.server.user.student.Student;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;


@NoArgsConstructor @AllArgsConstructor @Data @Builder
public class LectureView {
    private @JsonView(Views.Public.class) Long id;
    private @JsonView(Views.Public.class) String subject;
    private @JsonView(Views.Public.class) String teacher;
    private @JsonView(Views.Public.class) Date date;
    private @JsonView(Views.Public.class) List<Student> students;
}
