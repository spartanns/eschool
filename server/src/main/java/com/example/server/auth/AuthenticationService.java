package com.example.server.auth;

import com.example.server.auth.dao.AuthenticationResponse;
import com.example.server.auth.dto.LoginRequest;
import com.example.server.config.JwtService;
import com.example.server.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final JwtService service;
    private final AuthenticationManager manager;

    public AuthenticationResponse login(LoginRequest request) {
        manager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        var user = repository.findByUsername(request.getUsername()).orElseThrow();

        var token = service.generateToken(user);

        return AuthenticationResponse.builder().token(token).build();
    }
}
