package com.example.server.user.student;

import com.example.server.config.JwtService;
import com.example.server.management.grade.dao.GradeView;
import com.example.server.security.Views;
import com.example.server.user.User;
import com.example.server.user.UserRepository;
import com.example.server.user.UserService;
import com.example.server.user.student.dao.PrivateStudentView;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 *      TODO: Grades Search
 */

@RestController @RequiredArgsConstructor
@RequestMapping("/api/v1/students") @PreAuthorize("hasRole('STUDENT')")
public class StudentController {
    private final StudentService service;
    private final UserService userService;
    private final JwtService jwtService;

    @GetMapping("/{id}") @PreAuthorize("hasAuthority('student:read')") @JsonView(Views.Public.class)
    ResponseEntity<?> singleStudent(@RequestHeader("Authorization") String token, @PathVariable Long id) {

        try {
            User user = userService.readUserByUsername(jwtService.extractUsername(token.substring(7)));
            Student student = service.readStudent(id);

            if (user.getUsername().equals(student.getUser().getUsername())) {

                return new ResponseEntity<PrivateStudentView>(service.readPrivateStudentView(id), HttpStatus.OK);
            }

            return new ResponseEntity<String>("[UNAUTHORIZED]", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {

            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/grades") @PreAuthorize("hasAuthority('student:read')") @JsonView(Views.General.class)
    ResponseEntity<?> singleStudentGrades(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        try {
            User user = userService.readUserByUsername(jwtService.extractUsername(token.substring(7)));
            Student student = service.readStudent(id);

            if (user.getUsername().equals(student.getUser().getUsername())) {
                return new ResponseEntity<List<GradeView>>(service.readStudentGrades(id), HttpStatus.OK);
            }

            return new ResponseEntity<String>("[UNAUTHORIZED]", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{studentID}/grades/{gradeID}") @PreAuthorize("hasAuthority('student:read')") @JsonView(Views.Private.class)
    ResponseEntity<?> singleStudentGrade(@RequestHeader("Authorization") String token, @PathVariable Long studentID, @PathVariable Long gradeID) {
        try {
            User user = userService.readUserByUsername(jwtService.extractUsername(token.substring(7)));
            Student student = service.readStudent(studentID);

            if (user.getUsername().equals(student.getUser().getUsername())) {
                return new ResponseEntity<GradeView>(service.readStudentGrade(studentID, gradeID), HttpStatus.OK);
            }

            return new ResponseEntity<String>("[UNAUTHORIZED]", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
