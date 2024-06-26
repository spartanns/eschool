package com.example.server.user;

import com.example.server.auth.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service @RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder encoder;

    public List<User> index() {
        return repository.findAll();
    }

    public User createUser(RegisterRequest request) {
        var user = User
                .builder()
                .username(request.getUsername())
                .password(encoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        return repository.save(user);
    }

    public String deleteUser(Long id) {
            if (repository.findById(id) != null) {
                var user = repository.findById(id);
                repository.delete(user.orElseThrow(() -> new UsernameNotFoundException("User not found!")));

                return String.format("User with ID: %d deleted.", id);
            }

            return "User not found.";
    }

    public String updateUser(Long id, RegisterRequest request) {
        User user = repository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found."));

        user.setUsername(request.getUsername());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        repository.save(user);

        return String.format("User with ID: %d successfully updated.", user.getId());
    }

    public User readUser(Long id) {
        return repository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found."));
    }
}
