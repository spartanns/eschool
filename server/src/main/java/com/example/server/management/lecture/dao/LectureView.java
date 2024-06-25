package com.example.server.management.lecture.dao;

import com.example.server.management.department.Department;
import com.example.server.management.subject.Subject;
import com.example.server.user.teacher.Teacher;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor @AllArgsConstructor @Data @Builder
public class LectureView {
    private @JsonProperty("subject") Subject subject;
    private @JsonProperty("teacher") Teacher teacher;
    private @JsonProperty("department") Department dept;
}
