package com.example.server.management;

import com.example.server.management.grade.Grade;
import com.example.server.management.grade.GradeService;
import com.example.server.management.grade.dto.GradeRequest;
import com.example.server.management.lecture.Lecture;
import com.example.server.management.lecture.LectureService;
import com.example.server.management.lecture.dto.LectureRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController @RequiredArgsConstructor
@PreAuthorize("hasRole('MANAGER')") @RequestMapping("/api/v1/mgmt/lectures")
public class LectureController {
    private final LectureService service;
    private final GradeService gradeService;

    @PostMapping("/new") @PreAuthorize("hasAuthority('manager:create')")
    ResponseEntity<?> addNewLecture(@Valid @RequestBody LectureRequest request) {
        try {
            return new ResponseEntity<Lecture>(service.createLecture(request), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping @PreAuthorize("hasAuthority('manager:read')")
    ResponseEntity<List<Lecture>> getAllLectures() {
        return new ResponseEntity<List<Lecture>>(service.index(), HttpStatus.OK);
    }

    @PatchMapping("/{lectureID}/present") @PreAuthorize("hasAuthority('manager:update')")
    ResponseEntity<String> markStudentPresent(@PathVariable Long lectureID, @RequestParam Long studentID) {
        try {
            return new ResponseEntity<String>(service.markStudentPresent(lectureID, studentID), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/grades/new") @PreAuthorize("hasAuthority('manager:update')")
    ResponseEntity<?> addGradeToStudent(@Valid @RequestBody GradeRequest request) {
        try {
            return new ResponseEntity<Grade>(gradeService.createGrade(request), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
