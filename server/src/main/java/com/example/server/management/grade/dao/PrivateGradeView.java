package com.example.server.management.grade.dao;

import com.example.server.management.grade.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor @AllArgsConstructor @Data @Builder
public class PrivateGradeView {
    private Long id;
    private int value;
    private String subject;
    private Type type;
    private String createdBy;
    private Date createdAt;
}
