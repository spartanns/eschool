package com.example.server.management.subject;

import com.example.server.admin.department.Department;
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

    public List<Subject> index() {
        return repository.findAll();
    }

    public List<SingleSubjectView> readTeacherDeptSubjects(Long teacherID, Long deptID) {
        Teacher teacher = teacherRepository.findById(teacherID).orElseThrow(() -> new UsernameNotFoundException("Teacher not found."));
        List<SingleSubjectView> subjects = new ArrayList<>();
        List<LectureView> lectures = new ArrayList<>();

        for (Department d : teacher.getDepartments()) {
            if (d.getId().equals(deptID)) {
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
        }

        throw new UsernameNotFoundException("Dept not found.");
    }

    public SingleSubjectView readTeacherDeptSubject(Long teacherID, Long deptID, Long subjectID) {
        Teacher teacher = teacherRepository.findById(teacherID).orElseThrow(() -> new UsernameNotFoundException("Teacher not found."));
        List<LectureView> lectures = new ArrayList<>();

        for (Department d : teacher.getDepartments()) {
            if (d.getId().equals(deptID)) {
                for (Subject s : d.getSubjects()) {
                    if (s.getId().equals(subjectID)) {
                        for (Lecture l : s.getLectures()) {
                            LectureView lecture = LectureView
                                    .builder()
                                    .id(l.getId())
                                    .subject(s.getName())
                                    .teacher(String.format("%s %s", l.getTeacher().getName(), l.getTeacher().getSurname()))
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

                        return subject;
                    }
                }
            }
        }

        throw new UsernameNotFoundException("Subject not found.");
    }
}
