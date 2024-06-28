package com.example.server.management.subject;

import com.example.server.admin.department.Department;
import com.example.server.admin.department.DepartmentRepository;
import com.example.server.management.lecture.Lecture;
import com.example.server.management.lecture.dao.LectureView;
import com.example.server.management.subject.dao.SingleSubjectView;
import com.example.server.user.teacher.Teacher;
import com.example.server.user.teacher.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service @RequiredArgsConstructor
public class SubjectService {
    private final SubjectRepository repository;
    private final TeacherRepository teacherRepository;
    private final DepartmentRepository deptRepository;
/*
    public Subject createSubject(SubjectRequest request) {
        Department dept = deptRepository.findById(request.getDepartmentID()).orElse(null);

        var subject = Subject
                .builder()
                .name(request.getName())
                .semester(request.getSemester())
                .dept(dept)
                .build();

        return repository.save(subject);
    }
*/
    public List<Subject> index() {
        return repository.findAll();
    }

    public List<SingleSubjectView> readTeacherDeptSubjects(Long teacherID, Long deptID) {
        Teacher teacher = teacherRepository.findById(teacherID).orElseThrow(() -> new UsernameNotFoundException("Teacher not found."));
        List<Department> departments = teacher.getDepartments();
        Department d = null;
        List<SingleSubjectView> subjects = new ArrayList<>();
        List<LectureView> lectures = new ArrayList<>();

        for (Department dept : departments) {
            if (dept.getId().equals(deptID)) {
                d = dept;
            }
        }

        for (Subject s : d.getSubjects()) {
            for (Lecture l : s.getLectures()) {

                LectureView lecture = LectureView
                        .builder()
                        .id(l.getId())
                        .teacher(String.format("%s %s", l.getTeacher().getName(), l.getTeacher().getSurname()))
                        .subject(l.getSubject().getName())
                        .date(l.getCreatedAt())
                        .build();
                lectures.add(lecture);
            }

            SingleSubjectView subject = SingleSubjectView
                    .builder()
                    .id(s.getId())
                    .name(s.getName())
                    .semester(s.getSemester())
                    .lectures(lectures)
                    .build();
            subjects.add(subject);
        }

        return subjects;
    }

    public Subject readTeacherDeptSubject(Long teacherID, Long deptID, Long subjectID) {
        Teacher teacher = teacherRepository.findById(teacherID).orElseThrow(() -> new UsernameNotFoundException("Teacher not found."));
        Department d = null;
        Subject s = null;

        for (Department dept : teacher.getDepartments()) {
            if (dept.getId().equals(deptID)) {
                d = dept;
            }
        }

        for (Subject subject : d.getSubjects()) {
            if (subject.getId().equals(subjectID)) {
                s = subject;
            }
        }

        return s;
    }
}
