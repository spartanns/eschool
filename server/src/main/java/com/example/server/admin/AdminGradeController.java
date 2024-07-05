package com.example.server.admin;

import com.example.server.management.grade.Grade;
import com.example.server.management.grade.GradeService;
import com.example.server.management.grade.Type;
import com.example.server.management.grade.dao.AdminGradeView;
import com.example.server.security.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController @RequiredArgsConstructor
@RequestMapping("/api/v1/admin/grades") @PreAuthorize("hasRole('ADMIN')")
public class AdminGradeController {
    private final GradeService service;

    @GetMapping @PreAuthorize("hasAuthority('admin:read')")
    ResponseEntity<List<Grade>> getAllGrades() {
        return new ResponseEntity<List<Grade>>(service.index(), HttpStatus.OK);
    }

    @DeleteMapping("/{id}/delete") @PreAuthorize("hasAuthority('admin:delete')")
    ResponseEntity<String> deleteSingleGrade(@PathVariable Long id) {
        try {
            if (service.readGrade(id) != null) {

                return new ResponseEntity<String>(service.deleteGrade(id), HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<String>("Grade not found.", HttpStatus.NOT_FOUND);
        } catch (Exception e) {

            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}") @PreAuthorize("hasAuthority('admin:read')") @JsonView(Views.General.class)
    ResponseEntity<?> viewGrade(@PathVariable Long id) {
        try {
            return new ResponseEntity<AdminGradeView>(service.readAdminGradeView(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/new") @PreAuthorize("hasAuthority('admin:create')")
    ResponseEntity<?> createGrade(@RequestParam int value, @RequestParam Type type) {
        try {
            if (value < 1 || value > 5) {
                return new ResponseEntity<String>("Grade value must be between 1 and 5", HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<String>(service.createGrade(value, type), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{id}/student") @PreAuthorize("hasAuthority('admin:update')")
    ResponseEntity<String> updateGradeStudent(@PathVariable Long id, @RequestParam Long studentID) {
        try {
            return new ResponseEntity<String>(service.updateGradeStudent(id, studentID), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/{id}/subject") @PreAuthorize("hasAuthority('admin:update')")
    ResponseEntity<String> updateGradeSubject(@PathVariable Long id, @RequestParam Long subjectID) {
        try {
            return new ResponseEntity<String>(service.updateGradeSubject(id, subjectID), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/{id}/teacher") @PreAuthorize("hasAuthority('admin:update')")
    ResponseEntity<String> updateGradeTeacher(@PathVariable Long id, @RequestParam Long teacherID) {
        try {
            return new ResponseEntity<String>(service.updateGradeTeacher(id, teacherID), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/{id}/value") @PreAuthorize("hasAuthority('admin:update')")
    ResponseEntity<String> updateGradeValue(@PathVariable Long id, @RequestParam int value) {
        try {
            if (value < 1 || value > 5) {
                return new ResponseEntity<String>("Grade value must be between 1 and 5.", HttpStatus.BAD_REQUEST);
            }

            return new ResponseEntity<String>(service.updateGradeValue(id, value), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
