package com.example.server.user.parent;

import com.example.server.management.grade.Grade;
import com.example.server.management.grade.dao.GradeView;
import com.example.server.user.User;
import com.example.server.user.UserRepository;
import com.example.server.user.parent.dao.PrivateParentView;
import com.example.server.user.parent.dto.ParentRequest;
import com.example.server.user.student.Student;
import com.example.server.user.student.StudentRepository;
import com.example.server.user.student.dao.ParentStudentView;
import com.example.server.user.student.dao.PrivateStudentView;
import com.example.server.user.teacher.dao.GradeTeacherView;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public PrivateParentView readPPV(Parent parent) {
        List<ParentStudentView> students = new ArrayList<>();

        for (Student s : parent.getStudents()) {
            List<GradeView> grades = new ArrayList<>();
            for (Grade g : s.getGrades()) {

                GradeTeacherView teacher = GradeTeacherView
                        .builder()
                        .teacherID(g.getId())
                        .name(g.getCreatedBy().getName())
                        .surname(g.getCreatedBy().getSurname())
                        .build();

                GradeView grade = GradeView
                        .builder()
                        .gradeID(g.getId())
                        .value(g.getValue())
                        .type(g.getType())
                        .createdBy(teacher)
                        .createdAt(g.getCreatedAt())
                        .subject(g.getSubject().getName())
                        .semester(g.getSubject().getSemester())
                        .build();
                grades.add(grade);
            }
            ParentStudentView student = ParentStudentView
                    .builder()
                    .id(s.getId())
                    .name(s.getName())
                    .surname(s.getSurname())
                    .grades(grades)
                    .build();
            students.add(student);
        }

        PrivateParentView p = PrivateParentView
                .builder()
                .id(parent.getId())
                .students(students)
                .name(parent.getName())
                .surname(parent.getSurname())
                .email(parent.getEmail())
                .build();

        return p;
    }

    public List<ParentStudentView> readParentStudents(Parent parent) {
        PrivateParentView view = readPPV(parent);
        List<ParentStudentView> students = new ArrayList<>();

        return view.getStudents();
    }

    public ParentStudentView singleParentStudent(Parent parent, Long studentID) {
        PrivateParentView p = readPPV(parent);

        for (ParentStudentView s : p.getStudents()) {
            if (s.getId().equals(studentID)) {
                return s;
            }
        }

       throw new UsernameNotFoundException("Student not found");
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

    public List<GradeView> searchParentStudentGrades(Parent parent, String filterBy, String query) {
        List<GradeView> result = new ArrayList<>();

        switch (filterBy) {
            case "student":
                for (Student s : parent.getStudents()) {
                    if (s.getName().toLowerCase().startsWith(query.toLowerCase())) {
                        for (Grade g : s.getGrades()) {

                            GradeTeacherView teacher = GradeTeacherView
                                    .builder()
                                    .teacherID(g.getCreatedBy().getId())
                                    .name(g.getCreatedBy().getName())
                                    .surname(g.getCreatedBy().getSurname())
                                    .build();

                            GradeView grade = GradeView
                                    .builder()
                                    .gradeID(g.getId())
                                    .value(g.getValue())
                                    .subject(g.getSubject().getName())
                                    .type(g.getType())
                                    .semester(g.getSubject().getSemester())
                                    .createdAt(g.getCreatedAt())
                                    .createdBy(teacher)
                                    .build();
                            result.add(grade);
                        }
                    }
                }

                break;

            case "semester":
                for (Student s : parent.getStudents()) {
                    for (Grade g : s.getGrades()) {
                        if (g.getSubject().getSemester().name().toLowerCase().equals(query.toLowerCase())) {

                            GradeTeacherView teacher = GradeTeacherView
                                    .builder()
                                    .teacherID(g.getCreatedBy().getId())
                                    .name(g.getCreatedBy().getName())
                                    .surname(g.getCreatedBy().getSurname())
                                    .build();

                            GradeView grade = GradeView
                                    .builder()
                                    .gradeID(g.getId())
                                    .semester(g.getSubject().getSemester())
                                    .subject(g.getSubject().getName())
                                    .value(g.getValue())
                                    .createdBy(teacher)
                                    .createdAt(g.getCreatedAt())
                                    .type(g.getType())
                                    .build();
                            result.add(grade);
                        }
                    }
                }

                break;

            case "subject":
                for (Student s : parent.getStudents()) {
                    for (Grade g : s.getGrades()) {
                        if (g.getSubject().getName().toLowerCase().equals(query.toLowerCase())) {

                            GradeTeacherView teacher = GradeTeacherView
                                    .builder()
                                    .teacherID(g.getCreatedBy().getId())
                                    .name(g.getCreatedBy().getName())
                                    .surname(g.getCreatedBy().getSurname())
                                    .build();

                            GradeView grade = GradeView
                                    .builder()
                                    .gradeID(g.getId())
                                    .value(g.getValue())
                                    .subject(g.getSubject().getName())
                                    .semester(g.getSubject().getSemester())
                                    .type(g.getType())
                                    .createdAt(g.getCreatedAt())
                                    .createdBy(teacher)
                                    .build();
                            result.add(grade);
                        }
                    }
                }

                break;

            default:
                break;
        }

        return result;
    }
}
