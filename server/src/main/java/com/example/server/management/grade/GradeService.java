package com.example.server.management.grade;

import com.example.server.admin.department.Department;
import com.example.server.management.grade.dao.AdminGradeView;
import com.example.server.management.grade.dao.GradeView;
import com.example.server.management.grade.dao.TeacherGradeView;
import com.example.server.management.grade.dto.GradeRequest;
import com.example.server.management.lecture.Lecture;
import com.example.server.management.lecture.LectureRepository;
import com.example.server.management.lecture.dao.GradeLectureView;
import com.example.server.management.subject.Subject;
import com.example.server.management.subject.SubjectRepository;
import com.example.server.management.subject.dao.AdminSubjectView;
import com.example.server.management.subject.dao.LectureSubjectView;
import com.example.server.user.AdminUserView;
import com.example.server.user.student.dao.AdminStudentView;
import com.example.server.user.teacher.dao.AdminTeacherView;
import com.example.server.util.email.Email;
import com.example.server.util.email.EmailService;
import com.example.server.user.student.Student;
import com.example.server.user.student.StudentRepository;
import com.example.server.user.student.dao.GradeStudentView;
import com.example.server.user.teacher.Teacher;
import com.example.server.user.teacher.TeacherRepository;
import com.example.server.user.teacher.dao.GradeTeacherView;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service @RequiredArgsConstructor
public class GradeService {
    private final GradeRepository repository;
    private final StudentRepository studentRepository;
    private final LectureRepository lectureRepository;
    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;
    private final EmailService emailService;
    private Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

    public Grade readGrade(Long id) {
        return repository.findById(id).orElseThrow(() -> new UsernameNotFoundException("Grade not found."));
    }

    public List<Grade> index() {
        return repository.findAll();
    }

    public String deleteGrade(Long id) throws Exception {
        Grade grade = repository.findById(id).orElseThrow(() -> new Exception("Grade not found."));

        Email email = Email
                .builder()
                .to(grade.getStudent().getParent().getEmail())
                .subject(String.format("%s %s - Grade Deletion", grade.getStudent().getName(), grade.getStudent().getSurname()))
                .text(String.format("%s %s deleted %s %s's grade %d in %s.", grade.getCreatedBy().getName(), grade.getCreatedBy().getSurname(), grade.getStudent().getName(), grade.getStudent().getSurname(), grade.getValue(), grade.getSubject().getName()))
                .build();
        emailService.sendEmail(email);

        logger.warn(String.format("%s %s deleted %s %s's grade %d in %s.", grade.getCreatedBy().getName(), grade.getCreatedBy().getSurname(), grade.getStudent().getName(), grade.getStudent().getSurname(), grade.getValue(), grade.getSubject().getName()));

        String.format("%s %s deleted %s %s's grade %d in %s.", grade.getCreatedBy().getName(), grade.getCreatedBy().getSurname(), grade.getStudent().getName(), grade.getStudent().getSurname(), grade.getValue(), grade.getSubject().getName());

        repository.delete(grade);

        return String.format("Grade with ID: %d successfully deleted.", id);
    }

    public List<GradeView> readLectureStudentGrades(Long teacherID, Long deptID, Long subjectID, Long lectureID, Long studentID) {
        Teacher teacher = teacherRepository.findById(teacherID).orElseThrow(() -> new UsernameNotFoundException("Teacher not found."));
        List<GradeView> grades = new ArrayList<>();

        for (Department d : teacher.getDepartments()) {
            if (d.getId().equals(deptID)) {
                for (Subject s : d.getSubjects()) {
                    if (s.getId().equals(subjectID)) {
                        for (Lecture l : s.getLectures()) {
                            if (l.getId().equals(lectureID)) {
                                for (Student st : l.getDept().getStudents()) {
                                    for (Grade gr : st.getGrades()) {
                                        if (gr.getSubject().getId().equals(subjectID)) {
                                            GradeTeacherView teacherView = GradeTeacherView
                                                    .builder()
                                                    .teacherID(gr.getCreatedBy().getId())
                                                    .name(gr.getCreatedBy().getName())
                                                    .surname(gr.getCreatedBy().getSurname())
                                                    .build();

                                            GradeView grade = GradeView
                                                    .builder()
                                                    .gradeID(gr.getId())
                                                    .value(gr.getValue())
                                                    .subject(gr.getSubject().getName())
                                                    .type(gr.getType())
                                                    .semester(gr.getSubject().getSemester())
                                                    .createdBy(teacherView)
                                                    .build();

                                            grades.add(grade);
                                        }
                                    }
                                    return grades;
                                }
                            }
                        }
                    }
                }
            }
        }

        throw new UsernameNotFoundException("Student not found.");
    }

    public GradeView readLectureStudentGrade(Long teacherID, Long deptID, Long subjectID, Long lectureID, Long studentID, Long gradeID) {
        Teacher teacher = teacherRepository.findById(teacherID).orElseThrow(() -> new UsernameNotFoundException("Teacher not found."));

        for (Department d : teacher.getDepartments()) {
            if (d.getId().equals(deptID)) {
                for (Subject s : d.getSubjects()) {
                    if (s.getId().equals(subjectID)) {
                        for (Lecture l : s.getLectures()) {
                            if (l.getId().equals(lectureID)) {
                                for (Student st : d.getStudents()) {
                                    if (st.getId().equals(studentID)) {
                                        for (Grade g : st.getGrades()) {
                                            if (g.getSubject().getId().equals(subjectID)) {
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
                                                        .gradeID(gradeID)
                                                        .value(g.getValue())
                                                        .subject(g.getSubject().getName())
                                                        .type(g.getType())
                                                        .semester(g.getSubject().getSemester())
                                                        .createdBy(teacherView)
                                                        .lecture(lecture)
                                                        .createdAt(g.getCreatedAt())
                                                        .build();

                                                return grade;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        throw new UsernameNotFoundException("Grade not found.");
    }

    public String postLectureStudentGrade(Long teacherID, Long deptID, Long subjectID, Long lectureID, Long studentID, int value, Type type) {
        Teacher teacher = teacherRepository.findById(teacherID).orElseThrow(() -> new UsernameNotFoundException("Teacher not found."));

        for (Department d : teacher.getDepartments()) {
            if (d.getId().equals(deptID)) {
                for (Subject s : d.getSubjects()) {
                    if (s.getId().equals(subjectID)) {
                        for (Lecture l : s.getLectures()) {
                            if (l.getId().equals(lectureID)) {
                                for (Student student : d.getStudents()) {
                                    if (student.getId().equals(studentID)) {
                                        Grade grade = Grade
                                                .builder()
                                                .value(value)
                                                .type(type)
                                                .student(student)
                                                .createdBy(teacher)
                                                .createdAt(new Date(System.currentTimeMillis()))
                                                .lecture(l)
                                                .subject(s)
                                                .build();

                                        if (value < 1 || value > 5) {
                                            return "Grade value must be between 1 and 5.";
                                        }

                                        repository.save(grade);
                                        student.getGrades().add(grade);
                                        studentRepository.save(student);
                                        l.getGrades().add(grade);
                                        lectureRepository.save(l);
                                        s.getGrades().add(grade);
                                        subjectRepository.save(s);

                                        logger.info(String.format("%s %s gave %s %s a grade %d in %s.", teacher.getName(), teacher.getSurname(), student.getName(), student.getSurname(), grade.getValue(), s.getName()));

                                        Email email = Email
                                                .builder()
                                                .to(student.getParent().getEmail())
                                                .subject(String.format("%s %s - New Grade", student.getName(), student.getSurname()))
                                                .text(String.format("Subject: %s\nGrade: %d\nType: %s\nGiven by: %s %s\nDate: %s", grade.getSubject().getName(), grade.getValue(), grade.getType().name(), grade.getCreatedBy().getName(), grade.getCreatedBy().getSurname(), grade.getCreatedAt().toString()))
                                                .build();

                                        emailService.sendEmail(email);

                                        return String.format("%s %s gave %s %s a grade %d in %s.", teacher.getName(), teacher.getSurname(), student.getName(), student.getSurname(), grade.getValue(), s.getName());
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

    public String updateLectureStudentGrade(Long teacherID, Long deptID, Long subjectID, Long lectureID, Long studentID, Long gradeID, int value) {
        Teacher teacher = teacherRepository.findById(teacherID).orElseThrow(() -> new UsernameNotFoundException("Teacher not found."));

        for (Department d : teacher.getDepartments()) {
            if (d.getId().equals(deptID)) {
                for (Subject s : d.getSubjects()) {
                    if (s.getId().equals(subjectID)) {
                        for (Lecture l : s.getLectures()) {
                            if (l.getId().equals(lectureID)) {
                                for (Student st : d.getStudents()) {
                                    if (st.getId().equals(studentID)) {
                                        for (Grade g : st.getGrades()) {
                                            if (g.getId().equals(gradeID)) {
                                                g.setValue(value);
                                                g.setUpdatedAt(new Date(System.currentTimeMillis()));
                                                g.setUpdatedBy(teacher.getUser());
                                                repository.save(g);

                                                Email email = Email
                                                        .builder()
                                                        .to(g.getStudent().getParent().getEmail())
                                                        .subject(String.format("%s %s - Grade Update", g.getStudent().getName(), g.getStudent().getSurname()))
                                                        .text(String.format("%s %s's grade was updated to %d by %s.", g.getStudent().getName(), g.getStudent().getSurname(), g.getValue(), g.getUpdatedBy().getUsername()))
                                                        .build();
                                                emailService.sendEmail(email);

                                                logger.info(String.format("%s updated %s %s's grade to %d.", g.getUpdatedBy().getUsername(), g.getStudent().getName(), g.getStudent().getSurname(), g.getValue()));

                                                return String.format("%s updated %s %s's grade to %d.", g.getUpdatedBy().getUsername(), g.getStudent().getName(), g.getStudent().getSurname(), g.getValue());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        throw new UsernameNotFoundException("Grade not found.");
    }

    public String deleteLectureStudentGrade(Long teacherID, Long deptID, Long subjectID, Long lectureID, Long studentID, Long gradeID) {
        Teacher teacher = teacherRepository.findById(teacherID).orElseThrow(() -> new UsernameNotFoundException("Teacher not found."));

        for (Department d : teacher.getDepartments()) {
            if (d.getId().equals(deptID)) {
                for (Subject s : d.getSubjects()) {
                    if (s.getId().equals(subjectID)) {
                        for (Lecture l : s.getLectures()) {
                            if (l.getId().equals(lectureID)) {
                                for (Student st : d.getStudents()) {
                                    if (st.getId().equals(studentID)) {
                                        for (Grade g : l.getGrades()) {
                                            if (g.getId().equals(gradeID)) {
                                                Email email = Email
                                                        .builder()
                                                        .to(g.getStudent().getParent().getEmail())
                                                        .subject(String.format("%s %s - Grade Deletion", g.getStudent().getName(), g.getStudent().getSurname()))
                                                        .text(String.format("%s %s removed %s %s's grade %d from %s.\nDate: %s", teacher.getName(), teacher.getSurname(), g.getStudent().getName(), g.getStudent().getSurname(), g.getValue(), g.getSubject().getName(), new Date(System.currentTimeMillis()).toString()))
                                                        .build();
                                                emailService.sendEmail(email);

                                                logger.warn(String.format("%s %s removed %s %s's grade %d from %s.", teacher.getName(), teacher.getSurname(), st.getName(), st.getSurname(), g.getValue(), g.getSubject().getName()));

                                                repository.delete(g);

                                                return String.format("%s %s removed %s %s's grade %d from %s.", teacher.getName(), teacher.getSurname(), st.getName(), st.getSurname(), g.getValue(), g.getSubject().getName());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        throw new UsernameNotFoundException("Grade not found.");
    }

    public List<GradeView> readTeacherGrades(Long id) {
        Teacher teacher = teacherRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("Teacher not found."));
        List<GradeView> grades = new ArrayList<>();

        for (Grade g : teacher.getGrades()) {

            GradeTeacherView t = GradeTeacherView
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
                    .createdBy(t)
                    .createdAt(g.getCreatedAt())
                    .build();
            grades.add(grade);
        }

        return grades;
    }

    public TeacherGradeView readSingleTeacherGrade(Long id) {
        Grade grade = readGrade(id);

        GradeStudentView student = GradeStudentView
                .builder()
                .id(grade.getStudent().getId())
                .name(grade.getStudent().getName())
                .surname(grade.getStudent().getSurname())
                .build();

        TeacherGradeView view = TeacherGradeView
                .builder()
                .id(grade.getId())
                .value(grade.getValue())
                .type(grade.getType())
                .semester(grade.getSubject().getSemester())
                .subject(grade.getSubject().getName())
                .student(student)
                .lecture(grade.getLecture())
                .createdAt(grade.getCreatedAt())
                .updatedAt(grade.getUpdatedAt())
                .build();

        return view;
    }

    public List<TeacherGradeView> searchTeacherGrades(Long id, String filterBy, String query) {
       List<TeacherGradeView> result = new ArrayList<>();
       Teacher teacher = teacherRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("Teacher not found."));

        switch (filterBy) {
            case "value":
                for (Grade g : teacher.getGrades()) {
                    if (Integer.parseInt(query) == g.getValue()) {

                        GradeStudentView student = GradeStudentView
                                .builder()
                                .id(g.getStudent().getId())
                                .name(g.getStudent().getName())
                                .surname(g.getStudent().getSurname())
                                .build();

                        TeacherGradeView grade = TeacherGradeView
                                .builder()
                                .value(g.getValue())
                                .semester(g.getSubject().getSemester())
                                .lecture(g.getLecture())
                                .updatedAt(g.getUpdatedAt())
                                .student(student)
                                .subject(g.getSubject().getName())
                                .createdAt(g.getCreatedAt())
                                .build();
                        result.add(grade);
                    }
                }

                break;

            case "subject":
                for (Grade g : teacher.getGrades()) {
                    if (g.getSubject().getName().toLowerCase().equals(query.toLowerCase())) {

                        GradeStudentView student = GradeStudentView
                                .builder()
                                .id(g.getStudent().getId())
                                .name(g.getStudent().getName())
                                .surname(g.getStudent().getSurname())
                                .build();

                        TeacherGradeView grade = TeacherGradeView
                                .builder()
                                .value(g.getValue())
                                .semester(g.getSubject().getSemester())
                                .lecture(g.getLecture())
                                .updatedAt(g.getUpdatedAt())
                                .student(student)
                                .subject(g.getSubject().getName())
                                .createdAt(g.getCreatedAt())
                                .build();
                        result.add(grade);
                    }
                }

                break;

            case "semester":
                for (Grade g : teacher.getGrades()) {
                    if (g.getSubject().getSemester().name().toLowerCase().equals(query.toLowerCase())) {

                        GradeStudentView student = GradeStudentView
                                .builder()
                                .id(g.getStudent().getId())
                                .name(g.getStudent().getName())
                                .surname(g.getStudent().getSurname())
                                .build();

                        TeacherGradeView grade = TeacherGradeView
                                .builder()
                                .value(g.getValue())
                                .semester(g.getSubject().getSemester())
                                .lecture(g.getLecture())
                                .updatedAt(g.getUpdatedAt())
                                .student(student)
                                .subject(g.getSubject().getName())
                                .createdAt(g.getCreatedAt())
                                .build();
                        result.add(grade);
                    }
                }

                break;

            case "type":
                for (Grade g : teacher.getGrades()) {
                    if (g.getType().name().toLowerCase().equals(query.toLowerCase())) {

                        GradeStudentView student = GradeStudentView
                                .builder()
                                .id(g.getStudent().getId())
                                .name(g.getStudent().getName())
                                .surname(g.getStudent().getSurname())
                                .build();

                        TeacherGradeView grade = TeacherGradeView
                                .builder()
                                .value(g.getValue())
                                .semester(g.getSubject().getSemester())
                                .lecture(g.getLecture())
                                .updatedAt(g.getUpdatedAt())
                                .student(student)
                                .subject(g.getSubject().getName())
                                .createdAt(g.getCreatedAt())
                                .build();
                        result.add(grade);
                    }
                }

                break;

            case "student":
                for (Grade g : teacher.getGrades()) {
                    if (g.getStudent().getName().toLowerCase().startsWith(query.toLowerCase()) || g.getCreatedBy().getSurname().toLowerCase().startsWith(query.toLowerCase())) {

                        GradeStudentView student = GradeStudentView
                                .builder()
                                .id(g.getStudent().getId())
                                .name(g.getStudent().getName())
                                .surname(g.getStudent().getSurname())
                                .build();

                        TeacherGradeView grade = TeacherGradeView
                                .builder()
                                .id(g.getId())
                                .value(g.getValue())
                                .type(g.getType())
                                .semester(g.getSubject().getSemester())
                                .lecture(g.getLecture())
                                .updatedAt(g.getUpdatedAt())
                                .student(student)
                                .subject(g.getSubject().getName())
                                .createdAt(g.getCreatedAt())
                                .build();
                        result.add(grade);
                    }
                }

                break;

            case "date":
                for (Grade g : teacher.getGrades()) {
                    if (g.getCreatedAt().toString().startsWith(query)) {

                        GradeStudentView student = GradeStudentView
                                .builder()
                                .id(g.getStudent().getId())
                                .name(g.getStudent().getName())
                                .surname(g.getStudent().getSurname())
                                .build();

                        TeacherGradeView grade = TeacherGradeView
                                .builder()
                                .value(g.getValue())
                                .semester(g.getSubject().getSemester())
                                .lecture(g.getLecture())
                                .updatedAt(g.getUpdatedAt())
                                .student(student)
                                .subject(g.getSubject().getName())
                                .createdAt(g.getCreatedAt())
                                .build();
                        result.add(grade);
                    }
                }

                break;

            default:
                break;
        }

        return result;
    }

    public String updateTeacherGrade(Long teacherID, Long gradeID, int value) {
        Teacher teacher = teacherRepository.findById(teacherID).orElseThrow(() -> new UsernameNotFoundException("Teacher not found."));
        Grade grade = readGrade(gradeID);
        grade.setValue(value);
        grade.setUpdatedBy(teacher.getUser());
        grade.setUpdatedAt(new Date(System.currentTimeMillis()));
        repository.save(grade);

        Email email = Email
                .builder()
                .to(grade.getStudent().getParent().getEmail())
                .subject(String.format("%s %s - Grade Update", grade.getStudent().getName(), grade.getStudent().getSurname()))
                .text(String.format("%s %s updated %s %s's grade in %s to %d", teacher.getName(), teacher.getSurname(), grade.getStudent().getName(), grade.getStudent().getSurname(), grade.getSubject().getName(), grade.getValue()))
                .build();

        emailService.sendEmail(email);

        logger.info(String.format("%s %s updated %s %s's grade in %s to %d", teacher.getName(), teacher.getSurname(), grade.getStudent().getName(), grade.getStudent().getSurname(), grade.getSubject().getName(), grade.getValue()));

        return String.format("%s %s updated %s %s's grade in %s to %d", teacher.getName(), teacher.getSurname(), grade.getStudent().getName(), grade.getStudent().getSurname(), grade.getSubject().getName(), grade.getValue());
    }

    public AdminGradeView readAdminGradeView(Long id) {
        Grade g = readGrade(id);

        LectureSubjectView subject = LectureSubjectView
                .builder()
                .id(g.getSubject().getId())
                .semester(g.getSubject().getSemester())
                .hours(g.getSubject().getHours())
                .name(g.getSubject().getName())
                .build();

        AdminUserView teacherUser = AdminUserView
                .builder()
                .id(g.getCreatedBy().getUser().getId())
                .username(g.getCreatedBy().getUser().getUsername())
                .role(g.getCreatedBy().getUser().getRole())
                .build();

        AdminTeacherView teacher = AdminTeacherView
                .builder()
                .id(g.getCreatedBy().getId())
                .name(g.getCreatedBy().getName())
                .surname(g.getCreatedBy().getSurname())
                .user(teacherUser)
                .build();

        AdminUserView studentUser = AdminUserView
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
                .user(studentUser)
                .build();

        AdminGradeView grade = AdminGradeView
                .builder()
                .id(g.getId())
                .value(g.getValue())
                .type(g.getType())
                .createdAt(g.getCreatedAt())
                .updatedAt(g.getUpdatedAt())
                .subject(subject)
                .createdBy(teacher)
                .student(student)
                .build();

        return grade;
    }

    public String createGrade(int value, Type type) {
        Grade grade = Grade
                .builder()
                .value(value)
                .type(type)
                .createdAt(new Date(System.currentTimeMillis()))
                .build();

        repository.save(grade);

        logger.info(String.format("Grade of type %s with value %d created.", grade.getType().name(), grade.getValue()));

        return String.format("Grade of type %s with value %d created.", grade.getType().name(), grade.getValue());
    }

    public String updateGradeStudent(Long id, Long studentID) {
        Grade grade = readGrade(id);
        Student student = studentRepository.findById(studentID).orElseThrow(() -> new UsernameNotFoundException("Student not found."));
        grade.setStudent(student);
        grade.setUpdatedAt(new Date(System.currentTimeMillis()));
        repository.save(grade);
        student.getGrades().add(grade);
        studentRepository.save(student);

        Email email = Email
                .builder()
                .to(student.getParent().getEmail())
                .subject(String.format("%s %s - New Grade"))
                .text(String.format("%s %s gave %s %s a grade %d of type %s in %s.", grade.getCreatedBy().getName(), grade.getCreatedBy().getSurname(), grade.getStudent().getName(), grade.getStudent().getSurname(), grade.getValue(), grade.getType().name(), grade.getSubject().getName()))
                .build();
        emailService.sendEmail(email);

        logger.info(String.format("Grade with ID: %d added to student %s %s.", id, student.getName(), student.getSurname()));

        return String.format("Grade with ID: %d added to student %s %s.", id, student.getName(), student.getSurname());
    }

    public String updateGradeSubject(Long id, Long subjectID) {
        Grade grade = readGrade(id);
        Subject subject = subjectRepository.findById(subjectID).orElseThrow(() -> new UsernameNotFoundException("Subject not found."));
        grade.setSubject(subject);
        grade.setUpdatedAt(new Date(System.currentTimeMillis()));
        repository.save(grade);
        subject.getGrades().add(grade);
        subjectRepository.save(subject);

        logger.info(String.format("Grade with ID: %d added to subject %s.", id, subject.getName()));

        return String.format("Grade with ID: %d added to subject %s.", id, subject.getName());
    }

    public String updateGradeTeacher(Long id, Long teacherID) {
        Grade grade = readGrade(id);
        Teacher teacher = teacherRepository.findById(teacherID).orElseThrow(() -> new UsernameNotFoundException("Teacher not found."));
        grade.setCreatedBy(teacher);
        grade.setUpdatedAt(new Date(System.currentTimeMillis()));
        repository.save(grade);
        teacher.getGrades().add(grade);
        teacherRepository.save(teacher);

        logger.info(String.format("Grade with id: %d added to teacher %s %s.", id, teacher.getName(), teacher.getSurname()));

        return String.format("Grade with id: %d added to teacher %s %s.", id, teacher.getName(), teacher.getSurname());
    }

    public String updateGradeValue(Long id, int value) {
        Grade grade = readGrade(id);
        grade.setValue(value);
        grade.setUpdatedAt(new Date(System.currentTimeMillis()));
        repository.save(grade);

        Email email = Email
                .builder()
                .to(grade.getStudent().getParent().getEmail())
                .subject(String.format("%s %s - Grade Update", grade.getStudent().getName(), grade.getStudent().getSurname()))
                .text(String.format("%s %s updated %s %s's grade in %s to %d", grade.getCreatedBy().getName(), grade.getCreatedBy().getSurname(), grade.getStudent().getName(), grade.getStudent().getSurname(), grade.getSubject().getName(), grade.getValue()))
                .build();
        emailService.sendEmail(email);

        logger.info(String.format("Grade with ID: %d updated with value %d.", id, value));

        return String.format("Grade with ID: %d updated with value %d.", id, value);
    }
}
