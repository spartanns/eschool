package com.example.server.management.grade;

import com.example.server.management.grade.dto.GradeRequest;
import com.example.server.management.lecture.Lecture;
import com.example.server.management.lecture.LectureRepository;
import com.example.server.user.student.Student;
import com.example.server.user.student.StudentRepository;
import com.example.server.user.teacher.Teacher;
import com.example.server.user.teacher.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service @RequiredArgsConstructor
public class GradeService {
    private final GradeRepository repository;
    private final StudentRepository studentRepository;
    private final LectureRepository lectureRepository;
    private final TeacherRepository teacherRepository;

    public Grade createGrade(GradeRequest request) {
        Lecture lecture = lectureRepository.findById(request.getLectureID()).orElseThrow(() -> new UsernameNotFoundException("Lecture not found."));
        List<Student> students = lecture.getDept().getStudents();
        Student s = null;
        Teacher t = null;

        for (Student student : students) {
            if (student.getId().equals(request.getStudentID())) {
                s = student;
            }
        }

        for (Teacher teacher : lecture.getDept().getTeachers()) {
            if (teacher.getId().equals(request.getTeacherID())) {
                t = teacher;
            }
        }

        var grade = Grade
                .builder()
                .value(request.getValue())
                .type(request.getType())
                .createdBy(t)
                .student(s)
                .lecture(lecture)
                .createdAt(new Date(System.currentTimeMillis()))
                .build();
        repository.save(grade);

        s.getGrades().add(grade);
        studentRepository.save(s);

        t.getGrades().add(grade);
        teacherRepository.save(t);

        return grade;
    }
}
