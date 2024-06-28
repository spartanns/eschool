package com.example.server.management.lecture;

import com.example.server.admin.department.Department;
import com.example.server.admin.department.DepartmentRepository;
import com.example.server.management.lecture.dao.LectureView;
import com.example.server.management.lecture.dto.LectureRequest;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service @RequiredArgsConstructor
public class LectureService {
    private final LectureRepository repository;
    private final SubjectRepository subjectRepository;
    private final DepartmentRepository departmentRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

    public Lecture createLecture(Long teacherID, Long deptID, Long subjectID) {
        Teacher teacher = teacherRepository.findById(teacherID).orElseThrow(() -> new UsernameNotFoundException("Teacher not found."));
        List<Department> departments = teacher.getDepartments();
        Department dept = null;
        Subject s = null;

        for (Department department : departments) {
            if (department.getId().equals(deptID)) {
                dept = department;

                break;
            }
        }

        List<Subject> subjects = dept.getSubjects();

        for (Subject subject : subjects) {
            if (subject.getId().equals(subjectID)) {
                s = subject;

                break;
            }
        }

        var lecture = Lecture
                .builder()
                .teacher(teacher)
                .dept(dept)
                .subject(s)
                .createdAt(new Date(System.currentTimeMillis()))
                .build();


        s.setHours(s.getHours() + 1);

        subjectRepository.save(s);

        repository.save(lecture);

        logger.info(String.format("Lecture with ID: %d created by %s %s.", lecture.getId(), lecture.getTeacher().getName(), lecture.getTeacher().getSurname()));

        return lecture;
    }

    public Lecture readLecture(Long id) {
        return repository.findById(id).orElseThrow(() -> new UsernameNotFoundException("Lecture not found."));
    }

    public LectureView readLectureView(Long id) {
        Lecture lecture = repository.findById(id).orElseThrow(() -> new UsernameNotFoundException("Lecture not found."));
        LectureView view = LectureView
                .builder()
                .id(lecture.getId())
                .teacher(String.format("%s %s", lecture.getTeacher().getName(), lecture.getTeacher().getSurname()))
                .subject(lecture.getSubject().getName())
                .students(lecture.getDept().getStudents())
                .date(lecture.getCreatedAt())
                .build();

        return view;
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

    public List<LectureView> readSubjectLectures(Long teacherID, Long deptID, Long subjectID) {
        Teacher teacher = teacherRepository.findById(teacherID).orElseThrow(() -> new UsernameNotFoundException("Teacher not found."));
        Department department = null;
        Subject subject = null;
        Lecture lecture = null;
        List<LectureView> lectures = new ArrayList<>();

        for (Department d : teacher.getDepartments()) {
            if (d.getId().equals(deptID)) {
                department = d;
            }
        }

        for (Subject s : department.getSubjects()) {
            if (s.getId().equals(subjectID)) {
                subject = s;
            }
        }

        for (Lecture l : subject.getLectures()) {
            LectureView view = LectureView
                    .builder()
                    .id(l.getId())
                    .teacher(String.format("%s %s", l.getTeacher().getName(), l.getTeacher().getSurname()))
                    .date(l.getCreatedAt())
                    .subject(l.getSubject().getName())
                    .students(l.getDept().getStudents())
                    .build();
            lectures.add(view);
        }

        return lectures;
    }
}
