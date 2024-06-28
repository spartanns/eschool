package com.example.server.management;

import com.example.server.util.email.Email;
import com.example.server.util.email.EmailService;
import com.example.server.user.student.Student;
import com.example.server.user.student.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class ManagementService {
    private final StudentRepository repository;
    private final EmailService service;

    public String sendGlobalReport() {
        for (Student student : repository.findAll()) {
            if (student.getParent() == null) {
                continue;
            }

            Email email = Email
                    .builder()
                    .to(student.getParent().getEmail())
                    .subject(String.format("%s %s - GLOBAL SCHOOL REPORT", student.getName(), student.getSurname()))
                    .text("GLOBAL SCHOOL REPORT")
                    .build();
            service.sendEmail(email);
        }

        return "Global School Report sent.";
    }
}
