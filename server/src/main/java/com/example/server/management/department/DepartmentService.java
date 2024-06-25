package com.example.server.management.department;

import com.example.server.management.department.dto.DeptRequest;
import com.example.server.management.subject.Subject;
import com.example.server.management.subject.SubjectRepository;
import com.example.server.user.student.Student;
import com.example.server.user.student.StudentRepository;
import com.example.server.user.teacher.Teacher;
import com.example.server.user.teacher.TeacherRepository;
import lombok.RequiredArgsConstructor;
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

    public Department createDept(DeptRequest request) {
        Department dept = Department
                .builder()
                .name(request.getName())
                .build();

        return repository.save(dept);
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

        return String.format("Student with ID: %d added to department ID: %d", studentID, deptID);
    }

    public String addSubject(Long deptID, Long subjectID) {
        Department dept = repository.findById(deptID).orElseThrow(() -> new UsernameNotFoundException("Department not found."));
        Subject subject = subjectRepository.findById(subjectID).orElseThrow(() -> new UsernameNotFoundException("Subject not found."));

        subject.setDept(dept);
        subjectRepository.save(subject);

        dept.getSubjects().add(subject);
        repository.save(dept);

        return String.format("Subject with ID: %d added to department with ID: %d.", subjectID, deptID);
    }

    public Department addTeacher(Long deptID, Long teacherID) {
        Teacher teacher = teacherRepository.findById(teacherID).orElseThrow(() -> new UsernameNotFoundException("Teacher not found."));
        Department dept = deptRepository.findById(deptID).orElseThrow(() -> new UsernameNotFoundException("Department not found."));

        teacher.getDepartments().add(dept);
        teacherRepository.save(teacher);

        dept.getTeachers().add(teacher);

        return deptRepository.save(dept);
    }
}
