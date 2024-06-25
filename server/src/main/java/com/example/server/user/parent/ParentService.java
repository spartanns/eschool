package com.example.server.user.parent;

import com.example.server.user.User;
import com.example.server.user.UserRepository;
import com.example.server.user.parent.dto.ParentRequest;
import com.example.server.user.student.Student;
import com.example.server.user.student.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service @RequiredArgsConstructor
public class ParentService {
    private final ParentRepository repository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
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

    public Parent readParent(Long id) {
        return repository.findById(id).orElseThrow(() -> new UsernameNotFoundException("Parent not found."));
    }

    public String updateParentStudents(Long parentID, Long studentID) {
        Parent parent = repository.findById(parentID).orElseThrow(() -> new UsernameNotFoundException("Parent not found."));
        Student student = studentRepository.findById(studentID).orElseThrow(() -> new UsernameNotFoundException("Student not found."));

        student.setParent(parent);
        studentRepository.save(student);

        parent.getStudents().add(student);

        repository.save(parent);

        return String.format("Student %s %s added to parent %s %s.", student.getName(), student.getSurname(), parent.getName(), parent.getSurname());
    }

    public String deleteParent(Long id) {
        Parent parent = repository.findById(id).orElseThrow(() -> new UsernameNotFoundException("Parent not found."));
        repository.delete(parent);

        return String.format("Parent %s %s successfully deleted.", parent.getName(), parent.getSurname());
    }
}
