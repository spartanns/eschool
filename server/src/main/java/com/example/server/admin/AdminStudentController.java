package com.example.server.admin;

import com.example.server.user.student.Student;
import com.example.server.user.student.StudentService;
import com.example.server.user.student.dao.AdminStudentView;
import com.example.server.user.student.dto.StudentRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController @RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") @RequestMapping("/api/v1/admin/students")
public class AdminStudentController {
    private final StudentService service;

    @GetMapping @PreAuthorize("hasAuthority('admin:read')")
    ResponseEntity<List<AdminStudentView>> getAllStudents() {
        return new ResponseEntity<List<AdminStudentView>>(service.index(), HttpStatus.OK);
    }

    @GetMapping("/{id}") @PreAuthorize("hasAuthority('admin:read')")
    ResponseEntity<?> singleStudent(@PathVariable Long id) {
        try {
            return new ResponseEntity<Student>(service.readStudent(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/new") @PreAuthorize("hasAuthority('admin:create')")
    ResponseEntity<?> addStudent(@Valid @RequestBody StudentRequest request) {
        try {
            return new ResponseEntity<Student>(service.createStudent(request), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/{studentID}") @PreAuthorize("hasAuthority('admin:update')")
    ResponseEntity<?> updateStudentParent(@PathVariable Long studentID, @RequestParam Long parentID) {
        try {
            return new ResponseEntity<Student>(service.updateParent(studentID, parentID), HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}/delete") @PreAuthorize("hasAuthority('admin:delete')")
    ResponseEntity<String> deleteStudent(@PathVariable Long id) {
        try {
            return new ResponseEntity<String>(service.deleteStudent(id), HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/{id}/rating") @PreAuthorize("hasAuthority('admin:update')")
    ResponseEntity<String> setRating(@PathVariable Long id, @RequestParam int rating) {
        try {
            Student student = service.readStudent(id);
            student.setRating(rating);
            service.saveStudent(student);

            return new ResponseEntity<String>(String.format("Student %s %s's rating set to %d", student.getName(), student.getSurname(), rating), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{id}/dept") @PreAuthorize("hasAuthority('admin:update')")
    ResponseEntity<String> addDeptToStudent(@PathVariable Long id, @RequestParam Long deptID) {
        try {
            Student student = service.readStudent(id);

            return new ResponseEntity<String>(service.updateStudentDept(id, deptID), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
