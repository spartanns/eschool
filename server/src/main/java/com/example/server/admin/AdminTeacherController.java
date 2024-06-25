package com.example.server.admin;

import com.example.server.user.teacher.Teacher;
import com.example.server.user.teacher.TeacherService;
import com.example.server.user.teacher.dto.TeacherRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController @RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") @RequestMapping("/api/v1/admin/teachers")
public class AdminTeacherController {
    private final TeacherService service;

    @PostMapping("/new") @PreAuthorize("hasAuthority('admin:create')")
    ResponseEntity<?> addTeacher(@Valid @RequestBody TeacherRequest request) {
        try {
            return new ResponseEntity<Teacher>(service.createTeacher(request), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping @PreAuthorize("hasAuthority('admin:read')")
    ResponseEntity<List<Teacher>> getAllTeachers() {
        return new ResponseEntity<List<Teacher>>(service.index(), HttpStatus.OK);
    }
}
