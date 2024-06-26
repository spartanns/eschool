package com.example.server.management.subject;

import com.example.server.admin.department.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service @RequiredArgsConstructor
public class SubjectService {
    private final SubjectRepository repository;
    private final DepartmentRepository deptRepository;
/*
    public Subject createSubject(SubjectRequest request) {
        Department dept = deptRepository.findById(request.getDepartmentID()).orElse(null);

        var subject = Subject
                .builder()
                .name(request.getName())
                .semester(request.getSemester())
                .dept(dept)
                .build();

        return repository.save(subject);
    }
*/
    public List<Subject> index() {
        return repository.findAll();
    }
}
