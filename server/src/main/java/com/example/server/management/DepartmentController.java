package com.example.server.management;

import com.example.server.management.department.Department;
import com.example.server.management.department.DepartmentService;
import com.example.server.management.department.dto.DeptRequest;
import com.example.server.management.subject.SubjectRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController @RequiredArgsConstructor
@PreAuthorize("hasRole('MANAGER')") @RequestMapping("/api/v1/mgmt/departments")
public class DepartmentController {
    private final DepartmentService service;

    @GetMapping @PreAuthorize("hasAuthority('manager:read')")
    ResponseEntity<List<Department>> getAllDepts() {
        return new ResponseEntity<List<Department>>(service.index(), HttpStatus.OK);
    }

    @PostMapping("/new") @PreAuthorize("hasAuthority('manager:create')")
    ResponseEntity<?> addNewDept(@Valid @RequestBody DeptRequest request) {
        try {
            return new ResponseEntity<Department>(service.createDept(request), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/{deptID}/students/add") @PreAuthorize("hasAuthority('manager:update')")
    ResponseEntity<String> addStudentToDept(@PathVariable Long deptID, @RequestParam Long studentID) {
        try {
            return new ResponseEntity<String>(service.addStudent(deptID, studentID), HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/{deptID}/subjects/add") @PreAuthorize("hasAuthority('manager:update')")
    ResponseEntity<String> addSubjectToDept(@PathVariable Long deptID, @RequestParam Long subjectID) {
        try {
            return new ResponseEntity<String>(service.addSubject(deptID, subjectID), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/{deptID}/teachers/add") @PreAuthorize("hasAuthority('manager:update')")
    ResponseEntity<?> addTeacherToDept(@PathVariable Long deptID, @RequestParam Long teacherID) {
        try {
            return new ResponseEntity<Department>(service.addTeacher(deptID, teacherID), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
