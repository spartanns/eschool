package com.example.server.admin;

import com.example.server.user.parent.Parent;
import com.example.server.user.parent.ParentService;
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
public class ParentController {
    private final ParentService service;

    @GetMapping @PreAuthorize("hasAuthority('admin:read')")
    ResponseEntity<List<Parent>> getAllParents() {
        return new ResponseEntity<List<Parent>>(service.index(), HttpStatus.OK);
    }

    @PostMapping("/new") @PreAuthorize("hasAuthority('admin:create')")
    ResponseEntity<?> addNewParent(@Valid @RequestBody ParentRequest request) {
        try {
            return new ResponseEntity<Parent>(service.createParent(request), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
