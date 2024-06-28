package com.example.server.user.teacher;

import com.example.server.admin.department.Department;
import com.example.server.admin.department.DepartmentRepository;
import com.example.server.management.grade.GradeRepository;
import com.example.server.user.User;
import com.example.server.user.UserRepository;
import com.example.server.util.email.EmailService;
import com.example.server.user.student.StudentRepository;
import com.example.server.user.teacher.dto.TeacherRequest;
import com.example.server.user.teacher.dto.TeacherUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service @RequiredArgsConstructor
public class TeacherService {
    private final TeacherRepository repository;
    private final UserRepository userRepository;
    private final DepartmentRepository deptRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final GradeRepository gradeRepository;
    private final EmailService emailService;
    private final PasswordEncoder encoder;
    private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

    public Teacher createTeacher(TeacherRequest request) {
        // TODO: Better Automation?
        User user = User
                .builder()
                .username(request.getUsername())
                .password(encoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();
        userRepository.save(user);

        Teacher teacher = Teacher
                .builder()
                .name(request.getName())
                .surname(request.getSurname())
                .user(user)
                .build();


        repository.save(teacher);

        logger.info(String.format("Teacher %s %s created.", teacher.getName(), teacher.getSurname()));

        return teacher;
    }

    public List<Teacher> index() {
        return repository.findAll();
    }

    public Teacher readTeacher(Long id) {
        return repository.findById(id).orElseThrow(() -> new UsernameNotFoundException("Teacher not found!"));
    }

    public String updateTeacherDept(Long teacherID, Long deptID) {
        Teacher teacher = repository.findById(teacherID).orElseThrow(() -> new UsernameNotFoundException("Teacher not found."));
        Department department = deptRepository.findById(deptID).orElseThrow(() -> new UsernameNotFoundException("Department not found."));

        department.getTeachers().add(teacher);
        deptRepository.save(department);

        teacher.getDepartments().add(department);
        repository.save(teacher);

        logger.info(String.format("Teacher %s %s added to department %s.", teacher.getName(), teacher.getSurname(), department.getName()));

        return String.format("Teacher %s %s added to department %s.", teacher.getName(), teacher.getSurname(), department.getName());
    }

    public String updateTeacher(Long id, TeacherUpdateRequest request) {
        Teacher teacher = repository.findById(id).orElseThrow(() -> new UsernameNotFoundException("Teacher not found"));

        teacher.setName(request.getName());
        teacher.setSurname(request.getSurname());

        repository.save(teacher);

        return String.format("Teacher %s %s successfully updated.", teacher.getName(), teacher.getSurname());
    }

    public String deleteTeacher(Long id) {
        Teacher teacher = repository.findById(id).orElseThrow(() -> new UsernameNotFoundException("Teacher not found."));
        repository.delete(teacher);

        logger.warn(String.format("Teacher with ID: %d deleted.", id));

        return String.format("Teacher %s %s successfully deleted.", teacher.getName(), teacher.getSurname());
    }
}
