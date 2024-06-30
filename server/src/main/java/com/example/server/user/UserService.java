package com.example.server.user;

import com.example.server.auth.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service @RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

    public List<User> index() {
        return repository.findAll();
    }

    public User readUser(Long id) {
        return repository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found."));
    }

    public User readUserByUsername(String username) {
        return repository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found."));
    }

    public User createUser(RegisterRequest request) {
        User user = User
                .builder()
                .username(request.getUsername())
                .password(encoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();
        repository.save(user);

        logger.info(String.format("User with ID: %d created", user.getId()));

        return user;
    }

    public String deleteUser(Long id) {
        User user = readUser(id);
        repository.delete(user);

        logger.warn(String.format("User with ID: %d deleted.", user.getId()));

        return String.format("User with ID: %d deleted.", user.getId());
    }

    public String updateUser(Long id, RegisterRequest request) {
        User user = repository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found."));

        user.setUsername(request.getUsername());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        repository.save(user);

        logger.info(String.format("User with ID: %d updated.", user.getId()));

        return String.format("User with ID: %d successfully updated.", user.getId());
    }
}
