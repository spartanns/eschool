package com.example.server.admin.department;

import com.example.server.admin.department.dto.DeptRequest;
import com.example.server.management.subject.Subject;
import com.example.server.management.subject.SubjectRepository;
import com.example.server.user.student.Student;
import com.example.server.user.student.StudentRepository;
import com.example.server.user.teacher.Teacher;
import com.example.server.user.teacher.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service @RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository repository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;
    private final DepartmentRepository deptRepository;
    private Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

    public Department createDept(DeptRequest request) {
        Department dept = Department
                .builder()
                .name(request.getName())
                .build();

        logger.info(String.format("Department %s created.", dept.getName()));

        return repository.save(dept);
    }

    public Department readDept(Long id) {
        return repository.findById(id).orElseThrow(() -> new UsernameNotFoundException("Department not found."));
    }

    public List<Department> index() {
        return repository.findAll();
    }

    public String addStudent(Long deptID, Long studentID) {
        Department dept = repository.findById(deptID).orElseThrow(() -> new UsernameNotFoundException("Department not found."));
        Student student = studentRepository.findById(studentID).orElseThrow(() -> new UsernameNotFoundException("Student not found."));

        student.setDept(dept);
        studentRepository.save(student);

        dept.getStudents().add(student);
        repository.save(dept);

        logger.info(String.format("Student %s %s with ID: %d added to department %s", student.getName(), student.getSurname(), student.getId(), dept.getName()));

        return String.format("Student with ID: %d added to department ID: %d", studentID, deptID);
    }

    public String addSubject(Long deptID, Long subjectID) {
        Department dept = repository.findById(deptID).orElseThrow(() -> new UsernameNotFoundException("Department not found."));
        Subject subject = subjectRepository.findById(subjectID).orElseThrow(() -> new UsernameNotFoundException("Subject not found."));

        subject.setDept(dept);
        subjectRepository.save(subject);

        dept.getSubjects().add(subject);
        repository.save(dept);

        logger.info(String.format("Subject %s with ID: %d added to department %s.", subject.getName(), subject.getId(), dept.getName()));

        return String.format("Subject %s with ID: %d added to department %s.", subject.getName(), subject.getId(), dept.getName());
    }

    public String addTeacher(Long deptID, Long teacherID) {
        Teacher teacher = teacherRepository.findById(teacherID).orElseThrow(() -> new UsernameNotFoundException("Teacher not found."));
        Department dept = deptRepository.findById(deptID).orElseThrow(() -> new UsernameNotFoundException("Department not found."));

        teacher.getDepartments().add(dept);
        teacherRepository.save(teacher);

        dept.getTeachers().add(teacher);
        deptRepository.save(dept);

        logger.info(String.format("Teacher %s %s added to department %s.", teacher.getName(), teacher.getSurname(), dept.getName()));

        return String.format("Teacher %s %s added to department %s.", teacher.getName(), teacher.getSurname(), dept.getName());
    }

    public String deleteDept(Long id) {
        Department dept = repository.findById(id).orElseThrow(() -> new UsernameNotFoundException("Department not found."));
        repository.delete(dept);

        logger.warn(String.format("Department with ID: %d deleted.", id));

        return String.format("Department with ID: %d deleted.", id);
    }
}