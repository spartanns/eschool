package com.example.server.user.student;

import com.example.server.user.User;
import com.example.server.user.UserRepository;
import com.example.server.user.parent.Parent;
import com.example.server.user.parent.ParentRepository;
import com.example.server.user.student.dto.StudentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service @RequiredArgsConstructor
public class StudentService {
    private final StudentRepository repository;
    private final UserRepository userRepository;
    private final ParentRepository parentRepository;
    private final PasswordEncoder encoder;

    public Student createStudent(StudentRequest request) {
        var user = User
                .builder()
                .username(request.getUsername())
                .password(encoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();
        userRepository.save(user);

        var student = Student
                .builder()
                .name(request.getName())
                .surname(request.getSurname())
                .user(user)
                .parent(null)
                .dept(null)
                .grades(Collections.emptyList())
                .rating(5)
                .attended(0)
                .unattended(0)
                .build();

        return repository.save(student);
    }

    public List<Student> index() {
        return repository.findAll();
    }

    public Student updateParent(Long studentID, Long parentID) {
        Parent parent = parentRepository.findById(parentID).orElseThrow(() -> new UsernameNotFoundException("User not found."));
        Student student = repository.findById(studentID).orElseThrow(() -> new UsernameNotFoundException("User not found."));
        parent.getStudents().add(student);
        parentRepository.save(parent);

        student.setParent(parent);

        return repository.save(student);
    }

    public String deleteStudent(Long id) {
        Student student = repository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found."));
        repository.delete(student);

        return "Student deleted.";
    }
}
