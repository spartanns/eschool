package com.example.server.user.student;

import com.example.server.config.JwtService;
import com.example.server.user.User;
import com.example.server.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController @RequiredArgsConstructor
@RequestMapping("/api/v1/students") @PreAuthorize("hasRole('STUDENT')")
public class StudentController {
    private final StudentService service;
    private final JwtService jwtService;
    private final UserRepository repository;

    @GetMapping("/{id}") @PreAuthorize("hasAuthority('student:read')")
    ResponseEntity<?> singleStudent(@RequestHeader("Authorization") String token, @PathVariable Long id) {

        try {
            User user = repository.findByUsername(jwtService.extractUsername(token.substring(7))).orElseThrow(() -> new UsernameNotFoundException("Username not found."));
            Student student = service.readStudent(id);

            if (user.getUsername().equals(student.getUser().getUsername())) {

                return new ResponseEntity<Student>(student, HttpStatus.OK);
            }

            return new ResponseEntity<String>("[UNAUTHORIZED]", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {

            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
