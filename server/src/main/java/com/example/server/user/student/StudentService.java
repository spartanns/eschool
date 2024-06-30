package com.example.server.user.student;

import com.example.server.admin.department.Department;
import com.example.server.admin.department.DepartmentRepository;
import com.example.server.management.feedback.Feedback;
import com.example.server.management.feedback.dao.FeedbackView;
import com.example.server.management.grade.Grade;
import com.example.server.management.grade.dao.GradeView;
import com.example.server.management.lecture.Lecture;
import com.example.server.management.lecture.LectureRepository;
import com.example.server.management.lecture.dao.GradeLectureView;
import com.example.server.management.subject.Subject;
import com.example.server.user.Role;
import com.example.server.user.User;
import com.example.server.user.UserRepository;
import com.example.server.util.email.Email;
import com.example.server.util.email.EmailService;
import com.example.server.user.parent.Parent;
import com.example.server.user.parent.ParentRepository;
import com.example.server.user.student.dao.PrivateStudentView;
import com.example.server.user.student.dao.TeacherStudentView;
import com.example.server.user.student.dto.StudentRequest;
import com.example.server.user.teacher.Teacher;
import com.example.server.user.teacher.TeacherRepository;
import com.example.server.user.teacher.dao.GradeTeacherView;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service @RequiredArgsConstructor
public class StudentService {
    private final StudentRepository repository;
    private final UserRepository userRepository;
    private final ParentRepository parentRepository;
    private final TeacherRepository teacherRepository;
    private final LectureRepository lectureRepository;
    private final DepartmentRepository deptRepository;
    private final EmailService emailService;
    private final PasswordEncoder encoder;
    private Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

    public Student createStudent(StudentRequest request) {
        User user = User
                .builder()
                .username(request.getUsername())
                .password(encoder.encode(request.getPassword()))
                .role(Role.STUDENT)
                .build();
        userRepository.save(user);

        Student student = Student
                .builder()
                .name(request.getName())
                .surname(request.getSurname())
                .user(user)
                .parent(null)
                .dept(null)
                .grades(Collections.emptyList())
                .rating(5)
                .attended(0)
                .unattended(0)
                .build();


        repository.save(student);

        logger.info(String.format("Student %s %s with studentID %d created.", student.getName(), student.getSurname(), student.getId()));

        return student;
    }

    public List<Student> index() {
        return repository.findAll();
    }

    public Student saveStudent(Student student) {
        return repository.save(student);
    }

    public Student updateParent(Long studentID, Long parentID) {
        Parent parent = parentRepository.findById(parentID).orElseThrow(() -> new UsernameNotFoundException("User not found."));
        Student student = repository.findById(studentID).orElseThrow(() -> new UsernameNotFoundException("User not found."));

        parent.getStudents().add(student);
        parentRepository.save(parent);
        student.setParent(parent);

        logger.info(String.format("Parent %s %s assigned to student %s %s.", parent.getName(), parent.getSurname(), student.getName(), student.getSurname()));

        return repository.save(student);
    }

    public String deleteStudent(Long id) {
        Student student = repository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found."));

        repository.delete(student);

        logger.warn(String.format("Student with ID: %d deleted.", id));

        return "Student deleted.";
    }

    public Student readStudent(Long id) {
        return repository.findById(id).orElseThrow(() -> new UsernameNotFoundException("Student not found."));

    }

    public PrivateStudentView readPrivateStudentView(Long id) {
        Student student = readStudent(id);
        List<GradeView> grades = new ArrayList<>();
        List<FeedbackView> feedbacks = new ArrayList<>();

        for (Grade g : student.getGrades()) {

            GradeTeacherView teacherView = GradeTeacherView
                    .builder()
                    .teacherID(g.getCreatedBy().getId())
                    .name(g.getCreatedBy().getName())
                    .surname(g.getCreatedBy().getSurname())
                    .build();

            GradeView grade = GradeView
                    .builder()
                    .gradeID(g.getId())
                    .value(g.getValue())
                    .type(g.getType())
                    .semester(g.getSubject().getSemester())
                    .subject(g.getSubject().getName())
                    .createdBy(teacherView)
                    .build();
            grades.add(grade);
        }

        for (Feedback f : student.getFeedbacks()) {

            GradeTeacherView teacher = GradeTeacherView
                    .builder()
                    .teacherID(f.getCreatedBy().getId())
                    .name(f.getCreatedBy().getName())
                    .surname(f.getCreatedBy().getSurname())
                    .build();

            FeedbackView feedback = FeedbackView
                    .builder()
                    .id(f.getId())
                    .type(f.getType())
                    .text(f.getText())
                    .createdAt(f.getCreatedAt())
                    .createdBy(teacher)
                    .build();
            feedbacks.add(feedback);
        }

        PrivateStudentView view = PrivateStudentView
                .builder()
                .id(student.getId())
                .name(student.getName())
                .surname(student.getSurname())
                .rating(student.getRating())
                .feedbacks(feedbacks)
                .attended(student.getAttended())
                .unattended(student.getUnattended())
                .grades(grades)
                .build();

        return view;
    }

    public List<GradeView> readStudentGrades(Long id) {
        Student student = readStudent(id);
        List<GradeView> grades = new ArrayList<>();

        for (Grade g : student.getGrades()) {

            GradeTeacherView teacherView = GradeTeacherView
                    .builder()
                    .teacherID(g.getCreatedBy().getId())
                    .name(g.getCreatedBy().getName())
                    .surname(g.getCreatedBy().getSurname())
                    .build();

            GradeLectureView lecture = GradeLectureView
                    .builder()
                    .id(g.getLecture().getId())
                    .date(g.getLecture().getCreatedAt())
                    .build();

            GradeView grade = GradeView
                    .builder()
                    .gradeID(g.getId())
                    .value(g.getValue())
                    .subject(g.getSubject().getName())
                    .semester(g.getSubject().getSemester())
                    .type(g.getType())
                    .createdBy(teacherView)
                    .createdAt(g.getCreatedAt())
                    .lecture(lecture)
                    .build();
            grades.add(grade);
        }

        return grades;
    }

    public GradeView readStudentGrade(Long studentID, Long gradeID) {
        Student student = readStudent(studentID);

        for (Grade g : student.getGrades()) {
            if (g.getId().equals(gradeID)) {

                GradeTeacherView teacherView = GradeTeacherView
                        .builder()
                        .teacherID(g.getCreatedBy().getId())
                        .name(g.getCreatedBy().getName())
                        .surname(g.getCreatedBy().getSurname())
                        .build();

                GradeLectureView lecture = GradeLectureView
                        .builder()
                        .id(g.getLecture().getId())
                        .date(g.getLecture().getCreatedAt())
                        .build();

                GradeView grade = GradeView
                        .builder()
                        .gradeID(g.getId())
                        .value(g.getValue())
                        .type(g.getType())
                        .subject(g.getSubject().getName())
                        .semester(g.getSubject().getSemester())
                        .createdAt(g.getCreatedAt())
                        .lecture(lecture)
                        .createdBy(teacherView)
                        .build();

                return grade;
            }
        }

        throw new UsernameNotFoundException("Grade not found.");
    }

    public List<GradeView> searchStudentGrades(Long studentID, String filterBy, String query) {
        Student student = readStudent(studentID);
        List<GradeView> grades = new ArrayList<>();

        switch (filterBy) {
            case "value":
                for (Grade g : student.getGrades()) {
                    if (Integer.parseInt(query) == g.getValue()) {

                        GradeTeacherView teacherView = GradeTeacherView
                                .builder()
                                .teacherID(g.getCreatedBy().getId())
                                .name(g.getCreatedBy().getName())
                                .surname(g.getCreatedBy().getSurname())
                                .build();

                        GradeView grade = GradeView
                                .builder()
                                .gradeID(g.getId())
                                .value(g.getValue())
                                .type(g.getType())
                                .semester(g.getSubject().getSemester())
                                .subject(g.getSubject().getName())
                                .createdAt(g.getCreatedAt())
                                .createdBy(teacherView)
                                .build();
                        grades.add(grade);
                    }
                }

                break;

            case "subject":
                for (Grade g : student.getGrades()) {
                    if (g.getSubject().getName().toLowerCase().equals(query.toLowerCase())) {

                        GradeTeacherView teacher = GradeTeacherView
                                .builder()
                                .teacherID(g.getCreatedBy().getId())
                                .name(g.getCreatedBy().getName())
                                .surname(g.getCreatedBy().getSurname())
                                .build();

                        GradeView grade = GradeView
                                .builder()
                                .gradeID(g.getId())
                                .value(g.getValue())
                                .subject(g.getSubject().getName())
                                .type(g.getType())
                                .semester(g.getSubject().getSemester())
                                .createdBy(teacher)
                                .createdAt(g.getCreatedAt())
                                .build();
                        grades.add(grade);
                    }
                }

                break;

            case "semester":
                for (Grade g : student.getGrades()) {
                    if (g.getSubject().getSemester().name().toLowerCase().equals(query.toLowerCase())) {

                        GradeTeacherView teacher = GradeTeacherView
                                .builder()
                                .teacherID(g.getCreatedBy().getId())
                                .name(g.getCreatedBy().getName())
                                .surname(g.getCreatedBy().getSurname())
                                .build();

                        GradeView grade = GradeView
                                .builder()
                                .gradeID(g.getId())
                                .value(g.getValue())
                                .type(g.getType())
                                .subject(g.getSubject().getName())
                                .semester(g.getSubject().getSemester())
                                .createdAt(g.getCreatedAt())
                                .createdBy(teacher)
                                .build();
                        grades.add(grade);
                    }
                }

                break;

            case "type":
                for (Grade g : student.getGrades()) {
                    if (g.getType().name().toLowerCase().equals(query.toLowerCase())) {

                        GradeTeacherView teacher = GradeTeacherView
                                .builder()
                                .teacherID(g.getCreatedBy().getId())
                                .name(g.getCreatedBy().getName())
                                .surname(g.getCreatedBy().getSurname())
                                .build();

                        GradeView grade = GradeView
                                .builder()
                                .gradeID(g.getId())
                                .value(g.getValue())
                                .subject(g.getSubject().getName())
                                .semester(g.getSubject().getSemester())
                                .type(g.getType())
                                .createdBy(teacher)
                                .createdAt(g.getCreatedAt())
                                .build();
                        grades.add(grade);
                    }
                }

                break;

            case "teacher":
                for (Grade g : student.getGrades()) {
                    if (g.getCreatedBy().getName().toLowerCase().startsWith(query.toLowerCase()) || g.getCreatedBy().getSurname().toLowerCase().startsWith(query.toLowerCase())) {

                        GradeTeacherView teacher = GradeTeacherView
                                .builder()
                                .teacherID(g.getCreatedBy().getId())
                                .name(g.getCreatedBy().getName())
                                .surname(g.getCreatedBy().getSurname())
                                .build();

                        GradeView grade = GradeView
                                .builder()
                                .gradeID(g.getId())
                                .value(g.getValue())
                                .semester(g.getSubject().getSemester())
                                .subject(g.getSubject().getName())
                                .type(g.getType())
                                .createdAt(g.getCreatedAt())
                                .createdBy(teacher)
                                .build();
                        grades.add(grade);
                    }
                }

                break;

            case "date":
                for (Grade g : student.getGrades()) {
                    if (g.getCreatedAt().toString().startsWith(query)) {

                        GradeTeacherView teacher = GradeTeacherView
                                .builder()
                                .teacherID(g.getCreatedBy().getId())
                                .name(g.getCreatedBy().getName())
                                .surname(g.getCreatedBy().getSurname())
                                .build();

                        GradeView grade = GradeView
                                .builder()
                                .gradeID(g.getId())
                                .value(g.getValue())
                                .subject(g.getSubject().getName())
                                .type(g.getType())
                                .semester(g.getSubject().getSemester())
                                .createdBy(teacher)
                                .createdAt(g.getCreatedAt())
                                .build();
                        grades.add(grade);
                    }
                }

                break;

            default:
                break;
        }

        return grades;
    }

    public List<TeacherStudentView> readLectureStudents(Long teacherID, Long deptID, Long subjectID, Long lectureID) {
        Teacher teacher = teacherRepository.findById(teacherID).orElseThrow(() -> new UsernameNotFoundException("Teacher not found."));
        List<TeacherStudentView> students = new ArrayList<>();
        List<GradeView> grades = new ArrayList<>();

        for (Department d : teacher.getDepartments()) {
            if (d.getId().equals(deptID)) {
                for (Subject s : d.getSubjects()) {
                    if (s.getId().equals(subjectID)) {
                        for (Lecture l : s.getLectures()) {
                            if (l.getId().equals(lectureID)) {
                                for (Student st : l.getDept().getStudents()) {
                                    TeacherStudentView student = TeacherStudentView
                                            .builder()
                                            .id(st.getId())
                                            .name(st.getName())
                                            .surname(st.getSurname())
                                            .build();
                                    students.add(student);

                                }

                                return students;
                            }
                        }
                    }
                }
            }
        }

        throw new UsernameNotFoundException("Lecture not found.");
    }

    public TeacherStudentView readLectureStudent(Long teacherID, Long deptID, Long subjectID, Long lectureID, Long studentID) {
        Teacher teacher = teacherRepository.findById(teacherID).orElseThrow(() -> new UsernameNotFoundException("Teacher not found."));
        List<GradeView> grades = new ArrayList<>();

        for (Department d : teacher.getDepartments()) {
            if (d.getId().equals(deptID)) {
                for (Subject s : d.getSubjects()) {
                    if (s.getId().equals(subjectID)) {
                        for (Lecture l : s.getLectures()) {
                            if (l.getId().equals(lectureID)) {
                                for (Student st : d.getStudents()) {
                                    if (st.getId().equals(studentID)) {
                                        for (Grade g : s.getGrades()) {
                                            GradeTeacherView teacherView = GradeTeacherView
                                                    .builder()
                                                    .teacherID(g.getCreatedBy().getId())
                                                    .name(g.getCreatedBy().getName())
                                                    .surname(g.getCreatedBy().getSurname())
                                                    .build();

                                            GradeLectureView lecture = GradeLectureView
                                                    .builder()
                                                    .id(g.getLecture().getId())
                                                    .date(g.getLecture().getCreatedAt())
                                                    .build();

                                            GradeView grade = GradeView
                                                    .builder()
                                                    .gradeID(g.getId())
                                                    .value(g.getValue())
                                                    .subject(g.getSubject().getName())
                                                    .type(g.getType())
                                                    .createdAt(g.getCreatedAt())
                                                    .createdBy(teacherView)
                                                    .semester(g.getSubject().getSemester())
                                                    .lecture(lecture)
                                                    .build();

                                            if (g.getSubject().getId().equals(subjectID)) {
                                                grades.add(grade);
                                            }
                                        }

                                    TeacherStudentView student = TeacherStudentView
                                            .builder()
                                            .id(st.getId())
                                            .name(st.getName())
                                            .surname(st.getSurname())
                                            .grades(grades)
                                            .build();

                                        return student;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        throw new UsernameNotFoundException("Student not found.");
    }

    public String markStudentPresent(Long teacherID, Long deptID, Long subjectID, Long lectureID, Long studentID) {
        Teacher teacher = teacherRepository.findById(teacherID).orElseThrow(() -> new UsernameNotFoundException("Teacher not found."));

        for (Department d : teacher.getDepartments()) {
            if (d.getId().equals(deptID)) {
                for (Subject s : d.getSubjects()) {
                    if (s.getId().equals(subjectID)) {
                        for (Lecture l : s.getLectures()) {
                            if (l.getId().equals(lectureID)) {
                                for (Student student : l.getDept().getStudents()) {
                                    if (student.getId().equals(studentID)) {
                                        student.setAttended(student.getAttended() + 1);
                                        student.getLectures().add(l);
                                        repository.save(student);
                                        l.getAttendants().add(student);
                                        lectureRepository.save(l);

                                        return String.format("Student %s %s marked as present.", student.getName(), student.getSurname());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        throw new UsernameNotFoundException("Student not found.");
    }

    public String markStudentMissing(Long id) {
        Student student = readStudent(id);
        student.setUnattended(student.getUnattended() + 1);
        repository.save(student);

        if (student.getUnattended() > 0 && student.getUnattended() % 3 == 0) {
            student.setRating(student.getRating() - 1);
            repository.save(student);

            Email email = Email
                    .builder()
                    .to(student.getParent().getEmail())
                    .subject(String.format("%s %s - Rating Decreased", student.getName(), student.getSurname()))
                    .text(String.format("%s %s's school rating decreased.\nReason: skipping classes", student.getName(), student.getSurname()))
                    .build();

            emailService.sendEmail(email);
        }

        if (student.getRating() == 2) {
            Email email = Email
                    .builder()
                    .to(student.getParent().getEmail())
                    .subject(String.format("%s %s - Expulsion Notice", student.getName(), student.getSurname()))
                    .text(String.format("Student %s %s is about to be expelled from school.\nReason: low school rating", student.getName(), student.getSurname()))
                    .build();
            emailService.sendEmail(email);

            logger.warn(String.format("Student %s %s is about to be expelled from school.\nReason: low school rating", student.getName(), student.getSurname()));
        }

        return String.format("Student %s %s marked as missing.", student.getName(), student.getSurname());
    }

    public String updateStudentDept(Long studentID, Long deptID) {
        Student student = readStudent(studentID);
        Department dept = deptRepository.findById(deptID).orElseThrow(() -> new UsernameNotFoundException("Dept not found."));

        student.setDept(dept);
        repository.save(student);
        dept.getStudents().add(student);
        deptRepository.save(dept);

        logger.info(String.format("Student %s %s added to department %s.", student.getName(), student.getSurname(), dept.getName()));

        return String.format("Student %s %s added to department %s.", student.getName(), student.getSurname(), dept.getName());
    }
}
