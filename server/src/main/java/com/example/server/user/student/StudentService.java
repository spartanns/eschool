package com.example.server.user.student;

import com.example.server.management.grade.Grade;
import com.example.server.management.grade.dao.GradeView;
import com.example.server.user.User;
import com.example.server.user.UserRepository;
import com.example.server.user.parent.Parent;
import com.example.server.user.parent.ParentRepository;
import com.example.server.user.student.dao.PrivateStudentView;
import com.example.server.user.student.dto.StudentRequest;
import com.example.server.user.teacher.dao.GradeTeacherView;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service @RequiredArgsConstructor
public class StudentService {
    private final StudentRepository repository;
    private final UserRepository userRepository;
    private final ParentRepository parentRepository;
    private final PasswordEncoder encoder;
    private Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

    public Student createStudent(StudentRequest request) {
        User user = User
                .builder()
                .username(request.getUsername())
                .password(encoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();
        userRepository.save(user);

        Student student = Student
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

        logger.info(String.format("Student %s %s with studentID %d created.", student.getName(), student.getSurname(), student.getId()));

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

        logger.info(String.format("Parent %s %s assigned to student %s %s.", parent.getName(), parent.getSurname(), student.getName(), student.getSurname()));

        return repository.save(student);
    }

    public String deleteStudent(Long id) {
        Student student = repository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found."));

        repository.delete(student);

        logger.warn(String.format("Student with ID: %d deleted.", id));

        return "Student deleted.";
    }

    public Student readStudent(Long id) {
        return repository.findById(id).orElseThrow(() -> new UsernameNotFoundException("Student not found."));

    }

    public PrivateStudentView readPrivateStudentView(Long id) {
        Student student = readStudent(id);
        List<GradeView> grades = new ArrayList<>();

        for (Grade g : student.getGrades()) {

            GradeTeacherView teacherView = GradeTeacherView
                    .builder()
                    .teacherID(g.getCreatedBy().getId())
                    .name(g.getCreatedBy().getName())
                    .surname(g.getCreatedBy().getSurname())
                    .build();

            GradeView grade = GradeView
                    .builder()
                    .gradeID(g.getId())
                    .value(g.getValue())
                    .type(g.getType())
                    .semester(g.getSubject().getSemester())
                    .subject(g.getSubject().getName())
                    .createdBy(teacherView)
                    .build();
            grades.add(grade);
        }

        PrivateStudentView view = PrivateStudentView
                .builder()
                .id(student.getId())
                .name(student.getName())
                .surname(student.getSurname())
                .rating(student.getRating())
                .attended(student.getAttended())
                .unattended(student.getUnattended())
                .grades(grades)
                .build();

        return view;
    }

    public List<GradeView> readStudentGrades(Long id) {
        Student student = readStudent(id);
        List<GradeView> grades = new ArrayList<>();

        for (Grade g : student.getGrades()) {

            GradeTeacherView teacherView = GradeTeacherView
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
                    .createdBy(teacherView)
                    .createdAt(g.getCreatedAt())
                    .build();
            grades.add(grade);
        }

        return grades;
    }

    public GradeView readStudentGrade(Long studentID, Long gradeID) {
        Student student = readStudent(studentID);
        GradeView grade = null;

        for (Grade g : student.getGrades()) {
            if (g.getId().equals(gradeID)) {

                GradeTeacherView teacherView = GradeTeacherView
                        .builder()
                        .teacherID(g.getCreatedBy().getId())
                        .name(g.getCreatedBy().getName())
                        .surname(g.getCreatedBy().getSurname())
                        .build();

                grade = GradeView
                        .builder()
                        .gradeID(g.getId())
                        .value(g.getValue())
                        .type(g.getType())
                        .subject(g.getSubject().getName())
                        .semester(g.getSubject().getSemester())
                        .createdAt(g.getCreatedAt())
                        .createdBy(teacherView)
                        .build();
            }
        }

        return grade;
    }

    public List<GradeView> searchStudentGrades(Long studentID, String filterBy, String query) {
        Student student = readStudent(studentID);
        List<GradeView> grades = new ArrayList<>();

        switch (filterBy) {
            case "value":
                for (Grade g : student.getGrades()) {
                    if (Integer.parseInt(query) == g.getValue()) {

                        GradeTeacherView teacherView = GradeTeacherView
                                .builder()
                                .teacherID(g.getCreatedBy().getId())
                                .name(g.getCreatedBy().getName())
                                .surname(g.getCreatedBy().getSurname())
                                .build();

                        GradeView grade = GradeView
                                .builder()
                                .gradeID(g.getId())
                                .value(g.getValue())
                                .type(g.getType())
                                .semester(g.getSubject().getSemester())
                                .subject(g.getSubject().getName())
                                .createdAt(g.getCreatedAt())
                                .createdBy(teacherView)
                                .build();
                        grades.add(grade);
                    }
                }

                break;

            case "subject":
                for (Grade g : student.getGrades()) {
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
                                .type(g.getType())
                                .semester(g.getSubject().getSemester())
                                .createdBy(teacher)
                                .createdAt(g.getCreatedAt())
                                .build();
                        grades.add(grade);
                    }
                }

                break;

            case "semester":
                for (Grade g : student.getGrades()) {
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
                                .value(g.getValue())
                                .type(g.getType())
                                .subject(g.getSubject().getName())
                                .semester(g.getSubject().getSemester())
                                .createdAt(g.getCreatedAt())
                                .createdBy(teacher)
                                .build();
                        grades.add(grade);
                    }
                }

                break;

            case "type":
                for (Grade g : student.getGrades()) {
                    if (g.getType().name().toLowerCase().equals(query.toLowerCase())) {

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
                                .createdBy(teacher)
                                .createdAt(g.getCreatedAt())
                                .build();
                        grades.add(grade);
                    }
                }

                break;

            case "teacher":
                for (Grade g : student.getGrades()) {
                    if (g.getCreatedBy().getName().toLowerCase().startsWith(query.toLowerCase()) || g.getCreatedBy().getSurname().toLowerCase().startsWith(query.toLowerCase())) {

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
                                .semester(g.getSubject().getSemester())
                                .subject(g.getSubject().getName())
                                .type(g.getType())
                                .createdAt(g.getCreatedAt())
                                .createdBy(teacher)
                                .build();
                        grades.add(grade);
                    }
                }

                break;

            case "date":
                for (Grade g : student.getGrades()) {
                    if (g.getCreatedAt().toString().startsWith(query)) {

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
                                .createdBy(teacher)
                                .createdAt(g.getCreatedAt())
                                .build();
                        grades.add(grade);
                    }
                }

                break;

            default:
                break;
        }

        return grades;
    }
}
