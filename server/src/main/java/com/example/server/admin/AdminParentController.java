package com.example.server.admin;

import com.example.server.user.parent.Parent;
import com.example.server.user.parent.ParentService;
import com.example.server.user.parent.dao.AdminParentView;
import com.example.server.user.parent.dto.ParentRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController @RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") @RequestMapping("/api/v1/admin/parents")
public class AdminParentController {
    private final ParentService service;

    @GetMapping @PreAuthorize("hasAuthority('admin:read')")
    ResponseEntity<List<AdminParentView>> getAllParents() {
        return new ResponseEntity<List<AdminParentView>>(service.index(), HttpStatus.OK);
    }

    @PostMapping("/new") @PreAuthorize("hasAuthority('admin:create')")
    ResponseEntity<?> addNewParent(@Valid @RequestBody ParentRequest request) {
        try {
            return new ResponseEntity<Parent>(service.createParent(request), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}") @PreAuthorize("hasAuthority('admin:read')")
    ResponseEntity<?> singleParent(@PathVariable Long id) {
        try {
            return new ResponseEntity<Parent>(service.readParent(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{parentID}/students") @PreAuthorize("hasAuthority('admin:update')")
    ResponseEntity<String> addStudentsToParent(@PathVariable Long parentID, @RequestParam Long studentID) {
        try {
            return new ResponseEntity<String>(service.updateParentStudents(parentID, studentID), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}/delete") @PreAuthorize("hasAuthority('admin:delete')")
    ResponseEntity<String> deleteParent(@PathVariable Long id) {
        try {
            return new ResponseEntity<String>(service.deleteParent(id), HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
