package com.example.server.management.lecture;

import com.example.server.admin.department.Department;
import com.example.server.admin.department.dao.AdminDeptView;
import com.example.server.management.feedback.Feedback;
import com.example.server.management.feedback.dao.TeacherFeedbackView;
import com.example.server.management.grade.Grade;
import com.example.server.management.grade.dao.AdminGradeView;
import com.example.server.management.grade.dao.LectureGradeView;
import com.example.server.management.grade.dao.TeacherGradeView;
import com.example.server.management.lecture.dao.AdminLectureView;
import com.example.server.management.lecture.dao.LectureView;
import com.example.server.management.lecture.dao.TeacherLectureView;
import com.example.server.management.subject.Subject;
import com.example.server.management.subject.SubjectRepository;
import com.example.server.management.subject.dao.AdminSubjectView;
import com.example.server.management.subject.dao.LectureSubjectView;
import com.example.server.user.AdminUserView;
import com.example.server.user.student.Student;
import com.example.server.user.student.StudentRepository;
import com.example.server.user.student.dao.AdminStudentView;
import com.example.server.user.student.dao.GradeStudentView;
import com.example.server.user.teacher.Teacher;
import com.example.server.user.teacher.TeacherRepository;
import com.example.server.user.teacher.dao.AdminTeacherView;
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
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

    public Lecture createLecture(Long teacherID, Long deptID, Long subjectID) {
        Teacher teacher = teacherRepository.findById(teacherID).orElseThrow(() -> new UsernameNotFoundException("Teacher not found."));

        for (Department d : teacher.getDepartments()) {
            if (d.getId().equals(deptID)) {
                for (Subject s : d.getSubjects()) {
                    if (s.getId().equals(subjectID)) {
                        Lecture lecture = Lecture
                                .builder()
                                .subject(s)
                                .createdAt(new Date(System.currentTimeMillis()))
                                .teacher(teacher)
                                .dept(d)
                                .build();
                        s.setHours(s.getHours() + 1);
                        subjectRepository.save(s);
                        repository.save(lecture);

                        logger.info(String.format("Lecture with ID: %d created by %s %s.", lecture.getId(), lecture.getTeacher().getName(), lecture.getTeacher().getSurname()));

                        return lecture;
                    }
                }
            }
        }

       throw new UsernameNotFoundException("Subject not found.");
    }

    public Lecture readLecture(Long id) {
        return repository.findById(id).orElseThrow(() -> new UsernameNotFoundException("Lecture not found."));
    }

    public LectureView readLectureView(Long teacherID, Long deptID, Long subjectID, Long lectureID) {
        Teacher teacher = teacherRepository.findById(teacherID).orElseThrow(() -> new UsernameNotFoundException("Teacher not found."));
        List<GradeStudentView> students = new ArrayList<>();
        List<TeacherGradeView> grades = new ArrayList<>();
        List<TeacherFeedbackView> feedbacks = new ArrayList<>();

        for (Department d : teacher.getDepartments()) {
            if (d.getId().equals(deptID)) {
                for (Subject s : d.getSubjects()) {
                    if (s.getId().equals(subjectID)) {
                        for (Lecture l : s.getLectures()) {
                            if (l.getId().equals(lectureID)) {
                                for (Student st : l.getAttendants()) {
                                    GradeStudentView student = GradeStudentView
                                            .builder()
                                            .id(st.getId())
                                            .name(st.getName())
                                            .surname(st.getSurname())
                                            .build();
                                    students.add(student);
                                }


                                for (Feedback f : l.getFeedbacks()) {
                                    GradeStudentView student = GradeStudentView
                                            .builder()
                                            .id(f.getStudent().getId())
                                            .name(f.getStudent().getName())
                                            .surname(f.getStudent().getSurname())
                                            .build();

                                    TeacherFeedbackView feedback = TeacherFeedbackView
                                            .builder()
                                            .id(f.getId())
                                            .type(f.getType())
                                            .text(f.getText())
                                            .createdAt(f.getCreatedAt())
                                            .updatedAt(f.getUpdatedAt())
                                            .student(student)
                                            .build();
                                    feedbacks.add(feedback);
                                }

                                for (Grade g : l.getGrades()) {
                                    GradeStudentView student = GradeStudentView
                                            .builder()
                                            .id(g.getStudent().getId())
                                            .name(g.getStudent().getName())
                                            .surname(g.getStudent().getSurname())
                                            .build();

                                    TeacherGradeView grade = TeacherGradeView
                                            .builder()
                                            .id(g.getCreatedBy().getId())
                                            .value(g.getValue())
                                            .lecture(g.getLecture())
                                            .type(g.getType())
                                            .subject(g.getSubject().getName())
                                            .student(student)
                                            .semester(g.getSubject().getSemester())
                                            .build();
                                    grades.add(grade);
                                }

                                LectureView lecture = LectureView
                                        .builder()
                                        .id(l.getId())
                                        .teacher(String.format("%s %s", l.getTeacher().getName(), l.getTeacher().getSurname()))
                                        .subject(l.getSubject().getName())
                                        .attendants(students)
                                        .date(l.getCreatedAt())
                                        .grades(grades)
                                        .feedbacks(feedbacks) // TODO: Build feedbacks list
                                        .build();

                                return lecture;
                            }
                        }
                    }
                }
            }
        }

      throw new UsernameNotFoundException("Lecture not found.");
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

    public List<TeacherLectureView> readSubjectLectures(Long teacherID, Long deptID, Long subjectID) {
        Teacher teacher = teacherRepository.findById(teacherID).orElseThrow(() -> new UsernameNotFoundException("Teacher not found."));
        List<TeacherLectureView> lectures = new ArrayList<>();
        List<GradeStudentView> students = new ArrayList<>();

        for (Department d : teacher.getDepartments()) {
            if (d.getId().equals(deptID)) {
                for (Subject s : d.getSubjects()) {
                    if (s.getId().equals(subjectID)) {
                        for (Lecture l : s.getLectures()) {
                            for (Student st : l.getAttendants()) {
                                GradeStudentView student = GradeStudentView
                                        .builder()
                                        .id(st.getId())
                                        .name(st.getName())
                                        .surname(st.getSurname())
                                        .build();
                                students.add(student);
                            }
                            TeacherLectureView lecture = TeacherLectureView
                                    .builder()
                                    .id(l.getId())
                                    .attendants(students)
                                    .subject(l.getSubject().getName())
                                    .date(l.getCreatedAt())
                                    .build();
                            lectures.add(lecture);
                        }

                        return lectures;
                    }
                }
            }
        }
        throw new UsernameNotFoundException("Subject not found.");
    }

    public String deleteTeacherLecture(Long teacherID, Long lectureID) {
        Teacher teacher = teacherRepository.findById(teacherID).orElseThrow(() -> new UsernameNotFoundException("Teacher not found."));

        for (Lecture l : teacher.getLectures()) {
            if (l.getId().equals(lectureID)) {
                repository.delete(l);

                logger.warn(String.format("%s %s deleted a lecture with ID: %d", teacher.getName(), teacher.getSurname(), lectureID));

                return String.format("%s %s deleted a lecture with ID: %d", teacher.getName(), teacher.getSurname(), lectureID);
            }
        }

        throw new UsernameNotFoundException("Lecture not found.");
    }

    public TeacherLectureView readTeacherLecture(Long teacherID, Long lectureID) {
        Teacher teacher = teacherRepository.findById(teacherID).orElseThrow(() -> new UsernameNotFoundException("Teacher not found."));
        List<GradeStudentView> students = new ArrayList<>();
        List<TeacherFeedbackView> feedbacks = new ArrayList<>();
        List<TeacherGradeView> grades = new ArrayList<>();

        for (Lecture l : teacher.getLectures()) {
            if (l.getId().equals(lectureID)) {
                for (Student s : l.getAttendants()) {
                    GradeStudentView student = GradeStudentView
                            .builder()
                            .id(s.getId())
                            .name(s.getName())
                            .surname(s.getSurname())
                            .build();
                    students.add(student);
                }

                for (Feedback f: l.getFeedbacks()) {
                    GradeStudentView student = GradeStudentView
                            .builder()
                            .id(f.getStudent().getId())
                            .name(f.getStudent().getName())
                            .surname(f.getStudent().getSurname())
                            .build();

                    TeacherFeedbackView feedback = TeacherFeedbackView
                            .builder()
                            .id(f.getId())
                            .text(f.getText())
                            .createdAt(f.getCreatedAt())
                            .student(student)
                            .updatedAt(f.getUpdatedAt())
                            .type(f.getType())
                            .build();
                    feedbacks.add(feedback);
                }

                TeacherLectureView lecture = TeacherLectureView
                        .builder()
                        .id(l.getId())
                        .date(l.getCreatedAt())
                        .subject(l.getSubject().getName())
                        .attendants(students)
                        .feedbacks(feedbacks)
                        .grades(grades)
                        .build();

                return lecture;
            }
        }

        throw new UsernameNotFoundException("Lecture not found.");
    }

    public AdminLectureView readAdminLectureView(Long id) {
        Lecture l = readLecture(id);
        List<AdminStudentView> attendants = new ArrayList<>();
        List<LectureGradeView> grades = new ArrayList<>();

        AdminUserView teacherUser = AdminUserView
                .builder()
                .id(l.getTeacher().getUser().getId())
                .username(l.getTeacher().getUser().getUsername())
                .role(l.getTeacher().getUser().getRole())
                .build();

        AdminTeacherView teacher = AdminTeacherView
                .builder()
                .id(l.getTeacher().getId())
                .name(l.getTeacher().getName())
                .surname(l.getTeacher().getSurname())
                .user(teacherUser)
                .build();

        LectureSubjectView subject = LectureSubjectView
                .builder()
                .id(l.getSubject().getId())
                .name(l.getSubject().getName())
                .semester(l.getSubject().getSemester())
                .hours(l.getSubject().getHours())
                .build();

        for (Student s : l.getAttendants()) {
            AdminUserView studentUser = AdminUserView
                    .builder()
                    .id(s.getUser().getId())
                    .username(s.getUser().getUsername())
                    .role(s.getUser().getRole())
                    .build();

            AdminStudentView attendant = AdminStudentView
                    .builder()
                    .id(s.getId())
                    .name(s.getName())
                    .surname(s.getSurname())
                    .user(studentUser)
                    .build();
            attendants.add(attendant);
        }

        AdminDeptView dept = AdminDeptView
                .builder()
                .id(l.getSubject().getDept().getId())
                .name(l.getSubject().getDept().getName())
                .build();

        for (Grade g : l.getGrades()) {
            AdminUserView user = AdminUserView
                    .builder()
                    .id(g.getStudent().getUser().getId())
                    .username(g.getStudent().getUser().getUsername())
                    .role(g.getStudent().getUser().getRole())
                    .build();

            AdminStudentView student = AdminStudentView
                    .builder()
                    .id(g.getStudent().getId())
                    .name(g.getStudent().getName())
                    .surname(g.getStudent().getSurname())
                    .user(user)
                    .build();

            LectureGradeView grade = LectureGradeView
                    .builder()
                    .id(g.getId())
                    .value(g.getValue())
                    .type(g.getType())
                    .student(student)
                    .build();
            grades.add(grade);
        }

        AdminLectureView lecture = AdminLectureView
                .builder()
                .id(l.getId())
                .createdAt(l.getCreatedAt())
                .teacher(teacher)
                .subject(subject)
                .attendants(attendants)
                .feedbacks(l.getFeedbacks())
                .grades(grades)
                .department(dept)
                .build();

        return lecture;
    }

    public String deleteLecture(Long id) {
        Lecture l = readLecture(id);
        repository.delete(l);

        logger.warn(String.format("Lecture with ID: %d deleted.", id));

        return String.format("Lecture with ID: %d deleted.", id);
    }

    public Lecture adminCreateLecture() {
        Lecture lecture = Lecture
                .builder()
                .createdAt(new Date(System.currentTimeMillis()))
                .build();
        repository.save(lecture);

        logger.info(String.format("Lecture with ID: %d created at %s.", lecture.getId(), lecture.getCreatedAt().toString()));

        return lecture;
    }

    public String updateLectureTeacher(Long lectureID, Long teacherID) {
        Lecture lecture = readLecture(lectureID);
        Teacher teacher = teacherRepository.findById(teacherID).orElseThrow(() -> new UsernameNotFoundException("Teacher not found."));
        lecture.setTeacher(teacher);
        repository.save(lecture);
        teacher.getLectures().add(lecture);
        teacherRepository.save(teacher);

        logger.info(String.format("Teacher %s %s added to lecture ID: %d.", teacher.getName(), teacher.getSurname(), lecture.getId()));

        return String.format("Teacher %s %s added to lecture ID: %d.", teacher.getName(), teacher.getSurname(), lecture.getId());
    }
}
