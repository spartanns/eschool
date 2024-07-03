package com.example.server.user.teacher;

import com.example.server.admin.department.Department;
import com.example.server.admin.department.DepartmentRepository;
import com.example.server.admin.department.dao.SingleDeptView;
import com.example.server.management.feedback.Feedback;
import com.example.server.management.feedback.FeedbackRepository;
import com.example.server.management.feedback.dao.TeacherFeedbackView;
import com.example.server.management.feedback.dto.FeedbackRequest;
import com.example.server.management.grade.Grade;
import com.example.server.management.grade.dao.TeacherGradeView;
import com.example.server.management.lecture.Lecture;
import com.example.server.management.lecture.dao.LectureView;
import com.example.server.management.lecture.dao.TeacherLectureView;
import com.example.server.management.subject.Subject;
import com.example.server.management.subject.dao.SingleSubjectView;
import com.example.server.user.AdminUserView;
import com.example.server.user.Role;
import com.example.server.user.User;
import com.example.server.user.UserRepository;
import com.example.server.user.student.Student;
import com.example.server.user.student.dao.GradeStudentView;
import com.example.server.user.teacher.dao.AdminTeacherView;
import com.example.server.user.teacher.dao.TeacherView;
import com.example.server.util.email.Email;
import com.example.server.util.email.EmailService;
import com.example.server.user.teacher.dto.TeacherRequest;
import com.example.server.user.teacher.dto.TeacherUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service @RequiredArgsConstructor
public class TeacherService {
    private final TeacherRepository repository;
    private final UserRepository userRepository;
    private final DepartmentRepository deptRepository;
    private final FeedbackRepository feedbackRepository;
    private final EmailService emailService;
    private final PasswordEncoder encoder;
    private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

    public Teacher createTeacher(TeacherRequest request) {

        User user = User
                .builder()
                .username(request.getUsername())
                .password(encoder.encode(request.getPassword()))
                .role(Role.MANAGER)
                .build();
        userRepository.save(user);

        Teacher teacher = Teacher
                .builder()
                .name(request.getName())
                .surname(request.getSurname())
                .user(user)
                .build();


        repository.save(teacher);

        logger.info(String.format("Teacher %s %s created.", teacher.getName(), teacher.getSurname()));

        return teacher;
    }

    public List<AdminTeacherView> index() {
        List<AdminTeacherView> teachers = new ArrayList<>();

        for (Teacher t : repository.findAll()) {
            AdminUserView user = AdminUserView
                    .builder()
                    .id(t.getUser().getId())
                    .username(t.getUser().getUsername())
                    .role(t.getUser().getRole())
                    .build();
            AdminTeacherView teacher = AdminTeacherView
                    .builder()
                    .id(t.getId())
                    .name(t.getName())
                    .surname(t.getSurname())
                    .user(user)
                    .build();
            teachers.add(teacher);
        }

        return teachers;
    }

    public Teacher readTeacher(Long id) {
        return repository.findById(id).orElseThrow(() -> new UsernameNotFoundException("Teacher not found!"));
    }

    public String updateTeacherDept(Long teacherID, Long deptID) {
        Teacher teacher = repository.findById(teacherID).orElseThrow(() -> new UsernameNotFoundException("Teacher not found."));
        Department department = deptRepository.findById(deptID).orElseThrow(() -> new UsernameNotFoundException("Department not found."));

        department.getTeachers().add(teacher);
        deptRepository.save(department);

        teacher.getDepartments().add(department);
        repository.save(teacher);

        logger.info(String.format("Teacher %s %s added to department %s.", teacher.getName(), teacher.getSurname(), department.getName()));

        return String.format("Teacher %s %s added to department %s.", teacher.getName(), teacher.getSurname(), department.getName());
    }

    public String updateTeacher(Long id, TeacherUpdateRequest request) {
        Teacher teacher = repository.findById(id).orElseThrow(() -> new UsernameNotFoundException("Teacher not found"));

        teacher.setName(request.getName());
        teacher.setSurname(request.getSurname());

        repository.save(teacher);

        return String.format("Teacher %s %s successfully updated.", teacher.getName(), teacher.getSurname());
    }

    public String deleteTeacher(Long id) {
        Teacher teacher = repository.findById(id).orElseThrow(() -> new UsernameNotFoundException("Teacher not found."));
        repository.delete(teacher);

        logger.warn(String.format("Teacher with ID: %d deleted.", id));

        return String.format("Teacher %s %s successfully deleted.", teacher.getName(), teacher.getSurname());
    }

    public TeacherView readTeacherView(Long id) {
        Teacher teacher = readTeacher(id);
        List<TeacherGradeView> grades = new ArrayList<>();
        List<TeacherFeedbackView> feedbacks = new ArrayList<>();
        List<GradeStudentView> attendants = new ArrayList<>();
        List<LectureView> lectures = new ArrayList<>();
        List<SingleSubjectView> subjects = new ArrayList<>();
        List<SingleDeptView> departments = new ArrayList<>();

        for (Grade g : teacher.getGrades()) {
            GradeStudentView student = GradeStudentView
                    .builder()
                    .id(g.getStudent().getId())
                    .name(g.getStudent().getName())
                    .surname(g.getStudent().getSurname())
                    .build();
            TeacherGradeView grade = TeacherGradeView
                    .builder()
                    .id(g.getId())
                    .type(g.getType())
                    .value(g.getValue())
                    .subject(g.getSubject().getName())
                    .semester(g.getSubject().getSemester())
                    .createdAt(g.getCreatedAt())
                    .lecture(g.getLecture())
                    .student(student)
                    .updatedAt(g.getUpdatedAt())
                    .build();
            grades.add(grade);
        }

        for (Feedback f : teacher.getFeedbacks()) {
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
                    .student(student)
                    .createdAt(f.getCreatedAt())
                    .updatedAt(f.getUpdatedAt())
                    .build();
            feedbacks.add(feedback);
        }

        for (Department d : teacher.getDepartments()) {
            for (Subject s : d.getSubjects()) {
                for (Lecture l : s.getLectures()) {
                    for (Student st : l.getAttendants()) {
                        GradeStudentView student = GradeStudentView
                                .builder()
                                .id(s.getId())
                                .name(st.getName())
                                .surname(st.getSurname())
                                .build();
                        attendants.add(student);
                    }

                    LectureView lecture = LectureView
                            .builder()
                            .id(l.getId())
                            .teacher(String.format("%s %s", teacher.getName(), teacher.getSurname()))
                            .attendants(attendants)
                            .date(l.getCreatedAt())
                            .subject(l.getSubject().getName())
                            .build();
                    lectures.add(lecture);
                }

                SingleSubjectView subject = SingleSubjectView
                        .builder()
                        .id(s.getId())
                        .name(s.getName())
                        .semester(s.getSemester())
                        .build();
            }

            SingleDeptView dept = SingleDeptView
                    .builder()
                    .deptID(d.getId())
                    .name(d.getName())
                    .subjects(subjects)
                    .build();
            departments.add(dept);
        }

        TeacherView view = TeacherView
                .builder()
                .id(teacher.getId())
                .name(teacher.getName())
                .surname(teacher.getSurname())
                .departments(departments)
                .lectures(lectures)
                .grades(grades)
                .feedbacks(feedbacks)
                .build();

        return view;
    }

    public List<TeacherFeedbackView> readTeacherFeedbacks(Long id) {
        Teacher teacher = readTeacher(id);
        List<TeacherFeedbackView> feedbacks = new ArrayList<>();

        for (Feedback f : teacher.getFeedbacks()) {
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
                    .student(student)
                    .createdAt(f.getCreatedAt())
                    .updatedAt(f.getUpdatedAt())
                    .build();
            feedbacks.add(feedback);
        }

        return feedbacks;
    }

    public TeacherFeedbackView readTeacherFeedback(Long teacherID, Long feedbackID) {
        Teacher teacher = readTeacher(teacherID);

        for (Feedback f : teacher.getFeedbacks()) {
            if (f.getId().equals(feedbackID)) {
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
                        .student(student)
                        .createdAt(f.getCreatedAt())
                        .updatedAt(f.getUpdatedAt())
                        .build();

                return feedback;
            }
        }

        throw new UsernameNotFoundException("Feedback not found.");
    }

    public String updateTeacherFeedback(Long teacherID, Long feedbackID, FeedbackRequest request) {
        Teacher teacher = readTeacher(teacherID);

        for (Feedback f : teacher.getFeedbacks()) {
            if (f.getId().equals(feedbackID)) {
                f.setText(request.getText());
                f.setType(request.getType());
                f.setUpdatedAt(new Date(System.currentTimeMillis()));
                feedbackRepository.save(f);

                logger.info(String.format("%s %s updated %s %s's feedback to %s.", f.getCreatedBy().getName(), f.getCreatedBy().getSurname(), f.getStudent().getName(), f.getStudent().getSurname(), f.getType().name()));

                Email email = Email
                        .builder()
                        .to(f.getStudent().getParent().getEmail())
                        .subject(String.format("%s %s - Feedback Update", f.getStudent().getName(), f.getStudent().getSurname()))
                        .text(String.format("%s %s updated %s %s's feedback to %s.", f.getCreatedBy().getName(), f.getCreatedBy().getSurname(), f.getStudent().getName(), f.getStudent().getSurname(), f.getType().name()))
                        .build();

                emailService.sendEmail(email);

                return String.format("%s %s updated %s %s's feedback to %s.", f.getCreatedBy().getName(), f.getCreatedBy().getSurname(), f.getStudent().getName(), f.getStudent().getSurname(), f.getType().name());
            }
        }

        throw new UsernameNotFoundException("Feedback not found.");
    }

    public String deleteFeedback(Long teacherID, Long feedbackID) {
        Teacher teacher = readTeacher(teacherID);

        for (Feedback f : teacher.getFeedbacks()) {
            if (f.getId().equals(feedbackID)) {
                feedbackRepository.delete(f);

                logger.warn(String.format("%s %s removed %s %s's %s feedback.", f.getCreatedBy().getName(), f.getCreatedBy().getSurname(), f.getStudent().getName(), f.getStudent().getSurname(), f.getType().name()));

                Email email = Email
                        .builder()
                        .to(f.getStudent().getParent().getEmail())
                        .subject(String.format("%s %s - Feedback Deletion", f.getStudent().getName(), f.getStudent().getSurname()))
                        .text(String.format("%s %s removed %s %s's %s feedback.", f.getCreatedBy().getName(), f.getCreatedBy().getSurname(), f.getStudent().getName(), f.getStudent().getSurname(), f.getType().name()))
                        .build();

                emailService.sendEmail(email);

                return String.format("%s %s removed %s %s's %s feedback.", f.getCreatedBy().getName(), f.getCreatedBy().getSurname(), f.getStudent().getName(), f.getStudent().getSurname(), f.getType().name());
            }
        }

        throw new UsernameNotFoundException("Feedback not found.");
    }

    public List<TeacherLectureView> readTeacherLectures(Long id) {
        Teacher teacher = readTeacher(id);
        List<TeacherLectureView> lectures = new ArrayList<>();
        List<GradeStudentView> students = new ArrayList<>();

        for (Lecture l : teacher.getLectures()) {
            for (Student s : l.getDept().getStudents()) {
                GradeStudentView student = GradeStudentView
                        .builder()
                        .id(s.getId())
                        .name(s.getName())
                        .surname(s.getSurname())
                        .build();
                students.add(student);
            }

            TeacherLectureView lecture = TeacherLectureView
                    .builder()
                    .id(l.getId())
                    .date(l.getCreatedAt())
                    .subject(l.getSubject().getName())
                    .students(students)
                    .build();
            lectures.add(lecture);
        }

        return lectures;
    }
}
