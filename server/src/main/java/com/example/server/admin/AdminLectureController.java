package com.example.server.admin;

import com.example.server.management.lecture.Lecture;
import com.example.server.management.lecture.LectureService;
import com.example.server.management.lecture.dao.AdminLectureView;
import com.example.server.security.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController @RequiredArgsConstructor
@RequestMapping("/api/v1/admin/lectures") @PreAuthorize("hasRole('ADMIN')")
public class AdminLectureController {
    private final LectureService service;

    @GetMapping @PreAuthorize("hasAuthority('admin:read')") @JsonView(Views.Public.class)
    ResponseEntity<?> getAllLectures() {
        try {
            return new ResponseEntity<List<Lecture>>(service.index(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}") @PreAuthorize("hasAuthority('admin:read')") @JsonView(Views.General.class)
    ResponseEntity<?> getSingleLecture(@PathVariable Long id) {
        try {
            return new ResponseEntity<AdminLectureView>(service.readAdminLectureView(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}/delete") @PreAuthorize("hasAuthority('admin:delete')")
    ResponseEntity<String> deleteLecture(@PathVariable Long id) {
        try {
            return new ResponseEntity<String>(service.deleteLecture(id), HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/new") @PreAuthorize("hasAuthority('admin:create')")
    ResponseEntity<?> addNewLecture() {
        try {
            return new ResponseEntity<Lecture>(service.adminCreateLecture(), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{id}/teacher") @PreAuthorize("hasAuthority('admin:update')")
    ResponseEntity<String> addTeacherToLecture(@PathVariable Long id, @RequestParam Long teacherID) {
        try {
            return new ResponseEntity<String>(service.updateLectureTeacher(id, teacherID), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

}
