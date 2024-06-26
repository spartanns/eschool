package com.example.server.user.parent;

import com.example.server.config.JwtService;
import com.example.server.management.grade.Grade;
import com.example.server.management.grade.dao.PrivateGradeView;
import com.example.server.user.User;
import com.example.server.user.UserRepository;
import com.example.server.user.parent.dao.PrivateParentView;
import com.example.server.user.student.Student;
import com.example.server.user.student.dao.PrivateStudentView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController @RequiredArgsConstructor
@RequestMapping("/api/v1/parents") @PreAuthorize("hasRole('PARENT')")
public class ParentController {
    private final ParentService service;
    private final UserRepository repository;
    private final JwtService jwtService;

    @GetMapping("/{id}") @PreAuthorize("hasAuthority('parent:read')")
    ResponseEntity<?> singleParent(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        try {
            User user = repository.findByUsername(jwtService.extractUsername(token.substring(7))).orElseThrow(() -> new UsernameNotFoundException("User not found."));
            Parent parent = service.readParent(id);
            List<PrivateStudentView> students = new ArrayList<>();
            List<PrivateGradeView> grades = new ArrayList<>();

            for (Student student : parent.getStudents()) {
                for (Grade grade : student.getGrades()) {
                    PrivateGradeView view = PrivateGradeView
                            .builder()
                            .id(grade.getId())
                            .value(grade.getValue())
                            .subject(grade.getSubject().getName())
                            .type(grade.getType())
                            .createdBy(String.format("%s %s", grade.getCreatedBy().getName(), grade.getCreatedBy().getSurname()))
                            .build();
                    grades.add(view);
                }

                PrivateStudentView view = PrivateStudentView
                        .builder()
                        .id(student.getId())
                        .name(student.getName())
                        .surname(student.getSurname())
                        .grades(grades)
                        .build();
                students.add(view);
            }

            PrivateParentView view = PrivateParentView
                    .builder()
                    .id(parent.getId())
                    .name(parent.getName())
                    .surname(parent.getSurname())
                    .email(parent.getEmail())
                    .students(students)
                    .build();

            if (user.getUsername().equals(parent.getUser().getUsername())) {

                return new ResponseEntity<PrivateParentView>(view, HttpStatus.OK);
            }

            return new ResponseEntity<String>("[UNAUTHORIZED]", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {

            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
