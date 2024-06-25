package com.example.server.management.lecture;

import com.example.server.management.department.Department;
import com.example.server.management.department.DepartmentRepository;
import com.example.server.management.lecture.dto.LectureRequest;
import com.example.server.management.subject.Subject;
import com.example.server.management.subject.SubjectRepository;
import com.example.server.user.student.Student;
import com.example.server.user.student.StudentRepository;
import com.example.server.user.teacher.Teacher;
import com.example.server.user.teacher.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service @RequiredArgsConstructor
public class LectureService {
    private final LectureRepository repository;
    private final SubjectRepository subjectRepository;
    private final DepartmentRepository departmentRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;


    public Lecture createLecture(LectureRequest request) {
        Subject subject = subjectRepository.findById(request.getSubjectID()).orElseThrow(() -> new UsernameNotFoundException("Subject not found."));
        Department dept = departmentRepository.findById(request.getDeptID()).orElseThrow(() -> new UsernameNotFoundException("Department not found."));
        Teacher teacher = teacherRepository.findById(request.getTeacherID()).orElseThrow(() -> new UsernameNotFoundException("Teacher not found."));

        Lecture lecture = Lecture
                .builder()
                .subject(subject)
                .dept(dept)
                .teacher(teacher)
                .createdAt(new Date())
                .build();

        Subject newSubject = lecture.getSubject();
        newSubject.setHours(newSubject.getHours() + 1);
        subjectRepository.save(newSubject);

        return repository.save(lecture);
    }

    public List<Lecture> index() {
        return repository.findAll();
    }

    public String markStudentPresent(Long lectureID, Long studentID) {
        Lecture lecture = repository.findById(lectureID).orElseThrow(() -> new UsernameNotFoundException("Lecture not found."));

        for (Student student : lecture.getDept().getStudents()) {
            if (student.getId().equals(studentID)) {
                student.setAttended(student.getAttended() + 1);
                studentRepository.save(student);
            }
        }

        return String.format("Marked student with ID: %d as present.", studentID);
    }
}
