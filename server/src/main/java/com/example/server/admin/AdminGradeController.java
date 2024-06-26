package com.example.server.admin;

import com.example.server.management.grade.Grade;
import com.example.server.management.grade.GradeService;
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
}
