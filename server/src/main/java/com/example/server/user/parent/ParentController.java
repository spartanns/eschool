package com.example.server.user.parent;

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

            if (user.getUsername().equals(parent.getUser().getUsername())) {

                return new ResponseEntity<Parent>(parent, HttpStatus.OK);
            }

            return new ResponseEntity<String>("[UNAUTHORIZED]", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {

            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
