package com.example.server.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
}
