package com.example.server.admin;

import com.example.server.auth.dto.RegisterRequest;
import com.example.server.user.User;
import com.example.server.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController @RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") @RequestMapping("/api/v1/admin/users")
public class UserController {
   private final UserService service;
   private final PasswordEncoder encoder;

    @GetMapping @PreAuthorize("hasAuthority('admin:read')")
    ResponseEntity<?> showUsers() {
        try {
            return new ResponseEntity<List<User>>(service.index(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/new") @PreAuthorize("hasAuthority('admin:create')")
    ResponseEntity<?> addUsers(@Valid @RequestBody RegisterRequest request) {
        try {
            return new ResponseEntity<User>(service.createUser(request), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}") @PreAuthorize("hasAuthority('admin:delete')")
    ResponseEntity<?> removeUser(@Valid @PathVariable Long id) {
        try {
            return new ResponseEntity<String>(service.deleteUser(id), HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}

