package com.example.server.user.parent;

import com.example.server.config.JwtService;
import com.example.server.management.grade.Grade;
import com.example.server.management.grade.dao.GradeView;
import com.example.server.security.Views;
import com.example.server.user.User;
import com.example.server.user.UserRepository;
import com.example.server.user.UserService;
import com.example.server.user.parent.dao.PrivateParentView;
import com.example.server.user.student.dao.ParentStudentView;
import com.example.server.user.student.dao.PrivateStudentView;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.GregorianCalendar;
import java.util.List;

/*
 *          TODO: Grades Search
 */


@RestController @RequiredArgsConstructor
@RequestMapping("/api/v1/parents") @PreAuthorize("hasRole('PARENT')")
public class ParentController {
    private final ParentService service;
    private final UserService userService;
    private final JwtService jwtService;

    @GetMapping("/{id}") @PreAuthorize("hasAuthority('parent:read')") @JsonView(Views.Public.class)
    ResponseEntity<?> singleParent(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        try {
            User user = userService.readUserByUsername(jwtService.extractUsername(token.substring(7)));
            Parent parent = service.readParent(id);


            if (user.getUsername().equals(parent.getUser().getUsername())) {

                return new ResponseEntity<PrivateParentView>(service.readPPV(parent), HttpStatus.OK);
            }

            return new ResponseEntity<String>("[UNAUTHORIZED]", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {

            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/students") @PreAuthorize("hasAuthority('parent:read')") @JsonView(Views.Public.class)
    ResponseEntity<?> singleParentStudents(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        try {


            User user = userService.readUserByUsername(jwtService.extractUsername(token.substring(7)));
            Parent parent = service.readParent(id);

            if (user.getUsername().equals(parent.getUser().getUsername())) {
                return new ResponseEntity<List<ParentStudentView>>(service.readParentStudents(parent), HttpStatus.OK);
            }

            return new ResponseEntity<String>("[UNAUTHORIZED]", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{parentID}/students/{studentID}") @PreAuthorize("hasAuthority('parent:read')") @JsonView(Views.General.class)
    ResponseEntity<?> singleParentStudent(@RequestHeader("Authorization") String token, @PathVariable Long parentID, @PathVariable Long studentID) {
        try {
            User user = userService.readUserByUsername(jwtService.extractUsername(token.substring(7)));
            Parent parent = service.readParent(parentID);

            if (user.getUsername().equals(parent.getUser().getUsername())) {
                return new ResponseEntity<ParentStudentView>(service.singleParentStudent(parent, studentID), HttpStatus.OK);
            }

            return new ResponseEntity<String>("[UNAUTHORIZED]", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/grades/search") @PreAuthorize("hasAuthority('parent:read')") @JsonView(Views.Public.class)
    ResponseEntity<?> searchParentStudentGrades(@RequestHeader("Authorization") String token, @PathVariable Long id, @RequestParam String filterBy, @RequestParam String query) {
        try {
            User user = userService.readUserByUsername(jwtService.extractUsername(token.substring(7)));
            Parent parent = service.readParent(id);

            if (user.getUsername().equals(parent.getUser().getUsername())) {
                return new ResponseEntity<List<GradeView>>(service.searchParentStudentGrades(parent, filterBy, query), HttpStatus.OK);
            }

            return new ResponseEntity<String>("[UNAUTHORIZED]", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
