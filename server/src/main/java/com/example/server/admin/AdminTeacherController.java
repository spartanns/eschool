package com.example.server.admin;

import com.example.server.user.teacher.Teacher;
import com.example.server.user.teacher.TeacherService;
import com.example.server.user.teacher.dao.AdminTeacherView;
import com.example.server.user.teacher.dto.TeacherRequest;
import com.example.server.user.teacher.dto.TeacherUpdateRequest;
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
    ResponseEntity<List<AdminTeacherView>> getAllTeachers() {
        return new ResponseEntity<List<AdminTeacherView>>(service.index(), HttpStatus.OK);
    }

    @GetMapping("/{id}") @PreAuthorize("hasAuthority('admin:read')")
    ResponseEntity<?> singleTeacher(@PathVariable Long id) {
        try {
            return new ResponseEntity<Teacher>(service.readTeacher(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}/update") @PreAuthorize("hasAuthority('admin:update')")
    ResponseEntity<String> updateSingleTeacher(@PathVariable Long id, @Valid @RequestBody TeacherUpdateRequest request) {
        try {
            return new ResponseEntity<String>(service.updateTeacher(id, request), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{teacherID}/departments/add") @PreAuthorize("hasAuthority('admin:update')")
    ResponseEntity<String> addDeptToTeacher(@PathVariable Long teacherID, @RequestParam Long deptID) {
        try {
            return new ResponseEntity<String>(service.updateTeacherDept(teacherID, deptID), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}/delete") @PreAuthorize("hasAuthority('admin:delete')")
    ResponseEntity<String> deleteSingleTeacher(@PathVariable Long id) {
        try {
            return new ResponseEntity<String>(service.deleteTeacher(id), HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
