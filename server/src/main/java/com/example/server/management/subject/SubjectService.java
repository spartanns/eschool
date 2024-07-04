package com.example.server.management.subject;

import com.example.server.admin.department.Department;
import com.example.server.admin.department.DepartmentRepository;
import com.example.server.management.lecture.Lecture;
import com.example.server.management.lecture.dao.GradeLectureView;
import com.example.server.management.lecture.dao.LectureView;
import com.example.server.management.subject.dao.AdminSubjectView;
import com.example.server.management.subject.dao.SingleSubjectView;
import com.example.server.management.subject.dto.SubjectRequest;
import com.example.server.user.teacher.Teacher;
import com.example.server.user.teacher.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service @RequiredArgsConstructor
public class SubjectService {
    private final SubjectRepository repository;
    private final TeacherRepository teacherRepository;
    private final DepartmentRepository deptRepository;
    private Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

    public List<AdminSubjectView> index() {
        List<AdminSubjectView> subjects = new ArrayList<>();

        for (Subject s : repository.findAll()) {
            AdminSubjectView subject = AdminSubjectView
                    .builder()
                    .id(s.getId())
                    .name(s.getName())
                    .dept(s.getDept().getName())
                    .hours(s.getHours())
                    .grades(s.getGrades())
                    .lectures(s.getLectures())
                    .semester(s.getSemester())
                    .build();
            subjects.add(subject);
        }

        return subjects;
    }

    public String createSubject(SubjectRequest request) {
        Subject subject = Subject
                .builder()
                .name(request.getName())
                .semester(request.getSemester())
                .hours(0)
                .build();
        repository.save(subject);

        logger.info(String.format("Subject %s with ID: %d created.", subject.getName(), subject.getId()));

        return String.format("Subject %s with ID: %d created.", subject.getName(), subject.getId());
    }

    public Subject readSubject(Long id) {
        return repository.findById(id).orElseThrow(() -> new UsernameNotFoundException("Subject not found."));
    }

    public AdminSubjectView readAdminSubjectView(Long id) {
        Subject s = repository.findById(id).orElseThrow(() -> new UsernameNotFoundException("Subject not found"));

        AdminSubjectView subject = AdminSubjectView
                .builder()
                .id(s.getId())
                .name(s.getName())
                .dept(s.getDept().getName())
                .hours(s.getHours())
                .grades(s.getGrades())
                .semester(s.getSemester())
                .lectures(s.getLectures())
                .build();

        return subject;
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
        List<GradeLectureView> lectures = new ArrayList<>();

        for (Department d : teacher.getDepartments()) {
            if (d.getId().equals(deptID)) {
                for (Subject s : d.getSubjects()) {
                    if (s.getId().equals(subjectID)) {
                        for (Lecture l : s.getLectures()) {
                            GradeLectureView lecture = GradeLectureView
                                    .builder()
                                    .id(l.getId())
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

    public String updateDept(Long id, Long deptID) {
        Subject s = repository.findById(id).orElseThrow(() -> new UsernameNotFoundException("Subject not found."));
        Department d = deptRepository.findById(deptID).orElseThrow(() -> new UsernameNotFoundException("Department not found."));
        s.setDept(d);
        repository.save(s);
        d.getSubjects().add(s);
        deptRepository.save(d);

        logger.info(String.format("Subject %s with ID: %d assigned to department %s.", s.getName(), s.getId(), d.getName()));

        return String.format("Subject %s with ID: %d assigned to department %s.", s.getName(), s.getId(), d.getName());
    }

    public String deleteSubject(Long id) {
        Subject subject = readSubject(id);
        repository.delete(subject);

        logger.warn(String.format("Subject with ID: %d deleted.", id));

        return String.format("Subject with ID: %d deleted.", id);
    }
}
