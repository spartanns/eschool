package com.example.server.management.grade;

import com.example.server.management.grade.dto.GradeRequest;
import com.example.server.management.lecture.Lecture;
import com.example.server.management.lecture.LectureRepository;
import com.example.server.management.subject.Subject;
import com.example.server.user.student.Student;
import com.example.server.user.student.StudentRepository;
import com.example.server.user.teacher.Teacher;
import com.example.server.user.teacher.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

    public Grade readGrade(Long id) {
        return repository.findById(id).orElseThrow(() -> new UsernameNotFoundException("Grade not found."));
    }

    public Grade createGrade(GradeRequest request) {
        Lecture lecture = lectureRepository.findById(request.getLectureID()).orElseThrow(() -> new UsernameNotFoundException("Lecture not found."));
        Subject subject = lecture.getSubject();
        List<Student> students = lecture.getDept().getStudents();
        Student s = null;
        Teacher t = null;
        Subject sub = null;

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
                .subject(subject)
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

        logger.info(String.format("Teacher %s %s gave a grade %d to student %s %s in %s."), t.getName(), t.getSurname(), grade.getValue(), s.getName(), s.getSurname(), subject.getName());

        return grade;
    }

    public List<Grade> index() {
        return repository.findAll();
    }

    public String deleteGrade(Long id) throws Exception {
        Grade grade = repository.findById(id).orElseThrow(() -> new Exception("Grade not found."));
        repository.delete(grade);

        return String.format("Grade %d successfully deleted.", id);
    }
}
