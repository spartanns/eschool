package com.example.server.management.subject.dao;

import com.example.server.management.lecture.dao.LectureView;
import com.example.server.management.subject.Semester;
import com.example.server.security.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor @AllArgsConstructor @Data @Builder
public class SingleSubjectView {
    private @JsonView(Views.Public.class) Long id;
    private @JsonView(Views.Public.class) String name;
    private @JsonView(Views.Public.class) Semester semester;
    private @JsonView(Views.Private.class) List<LectureView> lectures;
}
