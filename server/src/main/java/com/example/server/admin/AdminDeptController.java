package com.example.server.admin;

import com.example.server.admin.department.Department;
import com.example.server.admin.department.DepartmentService;
import com.example.server.admin.department.dto.DeptRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping @PreAuthorize("hasAuthority('admin:read')")
    ResponseEntity<?> getAllDepts() {
        try {
            return new ResponseEntity<List<Department>>(service.index(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}") @PreAuthorize("hasAuthority('admin:read')")
    ResponseEntity<?> singleDept(@PathVariable Long id) {
        try {
            return new ResponseEntity<Department>(service.readDept(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{deptID}/teachers") @PreAuthorize("hasAuthority('admin:update')")
    ResponseEntity<String> addTeacherToDept(@PathVariable Long deptID, @RequestParam Long teacherID) {
        try {
            return new ResponseEntity<String>(service.addTeacher(deptID, teacherID), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}/delete") @PreAuthorize("hasAuthority('admin:delete')")
    ResponseEntity<String> deleteDept(@PathVariable Long id) {
        try {
            return new ResponseEntity<String>(service.deleteDept(id), HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
