package com.example.server.admin;

import com.example.server.management.subject.Subject;
import com.example.server.management.subject.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController @RequiredArgsConstructor
@RequestMapping("/api/v1/mgmt/subjects") @PreAuthorize("hasRole('MANAGER')")
public class AdminSubjectController {
    private final SubjectService service;

    @GetMapping @PreAuthorize("hasAuthority('manager:read')")
    ResponseEntity<List<Subject>> getAllSubjects() {
        return new ResponseEntity<List<Subject>>(service.index(), HttpStatus.OK);
    }
}
