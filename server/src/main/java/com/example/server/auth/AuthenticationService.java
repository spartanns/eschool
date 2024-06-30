package com.example.server.auth;

import com.example.server.auth.dao.AuthenticationResponse;
import com.example.server.auth.dto.LoginRequest;
import com.example.server.auth.dto.RegisterRequest;
import com.example.server.config.JwtService;
import com.example.server.user.User;
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

    public AuthenticationResponse register(RegisterRequest request) {
        var user = User
                .builder()
                .username(request.getUsername())
                .password(encoder.encode(request.getPassword()))
                .build();

        repository.save(user);

        var token = service.generateToken(user);

        return AuthenticationResponse.builder().token(token).build();
    }

    public AuthenticationResponse login(LoginRequest request) {
        manager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        var user = repository.findByUsername(request.getUsername()).orElseThrow();

        var token = service.generateToken(user);

        return AuthenticationResponse.builder().token(token).build();
    }
}
