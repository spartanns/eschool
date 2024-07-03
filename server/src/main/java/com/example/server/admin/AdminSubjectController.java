package com.example.server.admin;

import com.example.server.admin.department.Department;
import com.example.server.admin.department.DepartmentService;
import com.example.server.management.subject.Subject;
import com.example.server.management.subject.SubjectService;
import com.example.server.management.subject.dao.AdminSubjectView;
import com.example.server.management.subject.dto.SubjectRequest;
import com.example.server.security.Views;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController @RequiredArgsConstructor
@RequestMapping("/api/v1/admin/subjects") @PreAuthorize("hasRole('ADMIN')")
public class AdminSubjectController {
    private final SubjectService service;
    private final DepartmentService deptService;

    @GetMapping @PreAuthorize("hasAuthority('admin:read')") @JsonView(Views.Public.class)
    ResponseEntity<List<AdminSubjectView>> getAllSubjects() {
        return new ResponseEntity<List<AdminSubjectView>>(service.index(), HttpStatus.OK);
    }

    @GetMapping("/{id}") @PreAuthorize("hasAuthority('admin:read')") @JsonView(Views.Private.class)
    ResponseEntity<?> viewSubject(@PathVariable Long id) {
        try {
            return new ResponseEntity<AdminSubjectView>(service.readAdminSubjectView(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/{id}/dept") @PreAuthorize("hasAuthority('admin:update')")
        ResponseEntity<String> addDeptToSubject(@PathVariable Long id, @RequestParam Long deptID) {
            try {
                return new ResponseEntity<String>(service.updateDept(id, deptID), HttpStatus.CREATED);
            } catch (Exception e) {
                return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/new") @PreAuthorize("hasAuthority('admin:create')")
    ResponseEntity<String> addSubject(@Valid @RequestBody SubjectRequest request) {
        return new ResponseEntity<String>(service.createSubject(request), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}/delete") @PreAuthorize("hasAuthority('admin:delete')")
    ResponseEntity<String> deleteSubject(@PathVariable Long id) {
        try {
            return new ResponseEntity<String>(service.deleteSubject(id), HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

}
