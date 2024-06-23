package com.example.server.auth;

import com.example.server.auth.dao.AuthenticationResponse;
import com.example.server.auth.dto.LoginRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequiredArgsConstructor @RequestMapping("/api/v1/auth")
public class AuthenticationController {
    private final AuthenticationService service;

    @PostMapping("/login")
    ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody LoginRequest request) {
        return new ResponseEntity<AuthenticationResponse>(service.login(request), HttpStatus.CREATED);
    }
}
