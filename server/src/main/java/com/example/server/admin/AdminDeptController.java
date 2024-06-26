package com.example.server.admin;

import com.example.server.management.department.Department;
import com.example.server.management.department.DepartmentService;
import com.example.server.management.department.dto.DeptRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequiredArgsConstructor
@RequestMapping("/api/v1/admin/departments") @PreAuthorize("hasRole('ADMIN')")
public class AdminDeptController {
    private final DepartmentService service;

    @PostMapping("/new") @PreAuthorize("hasAuthority('admin:create')")
    ResponseEntity<?> addNewDept(@Valid @RequestBody DeptRequest request) {
        try {
            return new ResponseEntity<Department>(service.createDept(request), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
