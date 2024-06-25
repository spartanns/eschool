package com.example.server.user.parent;

import com.example.server.user.User;
import com.example.server.user.UserRepository;
import com.example.server.user.parent.dto.ParentRequest;
import jakarta.validation.constraints.NegativeOrZero;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.LifecycleState;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service @RequiredArgsConstructor
public class ParentService {
    private final ParentRepository repository;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public Parent createParent(ParentRequest request) {
        var user = User
                .builder()
                .username(request.getUsername())
                .password(encoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();
        userRepository.save(user);

        var parent = Parent
                .builder()
                .name(request.getName())
                .surname(request.getSurname())
                .email(request.getEmail())
                .user(user)
                .build();

        return repository.save(parent);
    }

    public List<Parent> index() {
        return repository.findAll();
    }
}
