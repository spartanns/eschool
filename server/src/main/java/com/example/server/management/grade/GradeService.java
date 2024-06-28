package com.example.server.management.grade;

import com.example.server.admin.department.Department;
import com.example.server.management.grade.dao.GradeView;
import com.example.server.management.grade.dao.TeacherGradeView;
import com.example.server.management.grade.dto.GradeRequest;
import com.example.server.management.lecture.Lecture;
import com.example.server.management.lecture.LectureRepository;
import com.example.server.management.subject.Subject;
import com.example.server.user.email.Email;
import com.example.server.user.email.EmailService;
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
    private final EmailService emailService;
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

        logger.warn(String.format("Grade %d successfully deleted.", id));

        return String.format("Grade %d successfully deleted.", id);
    }

    public List<GradeView> readLectureStudentGrades(Long teacherID, Long deptID, Long subjectID, Long lectureID, Long studentID) {
        Teacher teacher = teacherRepository.findById(teacherID).orElseThrow(() -> new UsernameNotFoundException("Teacher not found."));
        Department dept = null;
        Lecture l = null;
        Student s = null;
        List<GradeView> grades = new ArrayList<>();

        for (Department department : teacher.getDepartments()) {
            if (department.getId().equals(deptID)) {
                dept = department;
            }
        }

        for (Lecture lecture : dept.getLectures()) {
            if (lecture.getId().equals(lectureID)) {
                l = lecture;
            }
        }

        for (Student student : l.getDept().getStudents()) {
            if (student.getId().equals(studentID)) {
                s = student;
            }
        }

        for (Grade grade : s.getGrades()) {
            if (grade.getSubject().getId().equals(subjectID)) {
                GradeTeacherView teacherView = GradeTeacherView
                        .builder()
                        .teacherID(grade.getCreatedBy().getId())
                        .name(grade.getCreatedBy().getName())
                        .surname(grade.getCreatedBy().getSurname())
                        .build();

                GradeView view = GradeView
                        .builder()
                        .gradeID(grade.getId())
                        .value(grade.getValue())
                        .subject(grade.getSubject().getName())
                        .type(grade.getType())
                        .semester(grade.getSubject().getSemester())
                        .createdBy(teacherView)
                        .build();

                grades.add(view);
            }
        }

        return grades;
    }

    public GradeView readLectureStudentGrade(Long teacherID, Long deptID, Long subjectID, Long lectureID, Long studentID, Long gradeID) {
        Teacher teacher = teacherRepository.findById(teacherID).orElseThrow(() -> new UsernameNotFoundException("Teacher not found."));
        Department department = null;
        Subject subject = null;
        Lecture lecture = null;
        Student student = null;
        GradeView grade = null;

        for (Department dept : teacher.getDepartments()) {
            if (dept.getId().equals(deptID)) {
                department = dept;
            }
        }

        for (Subject s : department.getSubjects()) {
            if (s.getId().equals(subjectID)) {
                subject = s;
            }
        }

        for (Lecture l : subject.getLectures()) {
            if (l.getId().equals(lectureID)) {
                lecture = l;
            }
        }

        for (Student s : lecture.getDept().getStudents()) {
            if (s.getId().equals(studentID)) {
                student = s;
            }
        }

        for (Grade g : student.getGrades()) {
            if (g.getSubject().getId().equals(subjectID)) {
                GradeTeacherView teacherView = GradeTeacherView
                        .builder()
                        .teacherID(g.getCreatedBy().getId())
                        .name(g.getCreatedBy().getName())
                        .surname(g.getCreatedBy().getSurname())
                        .build();

                GradeView view = GradeView
                        .builder()
                        .gradeID(gradeID)
                        .value(g.getValue())
                        .subject(g.getSubject().getName())
                        .type(g.getType())
                        .semester(g.getSubject().getSemester())
                        .createdBy(teacherView)
                        .createdAt(g.getCreatedAt())
                        .build();
                grade = view;
            }

        }

        return grade;
    }

    public String postLectureStudentGrade(Long teacherID, Long deptID, Long subjectID, Long lectureID, Long studentID, int value, Type type) {
        Teacher teacher = teacherRepository.findById(teacherID).orElseThrow(() -> new UsernameNotFoundException("Teacher not found."));
        Subject subject = null;
        Department d = null;
        Lecture l = null;
        Student s = null;

        for (Department dept : teacher.getDepartments()) {
            if (dept.getId().equals(deptID)) {
                d = dept;
            }
        }

        for (Subject subj : d.getSubjects()) {
            if (subj.getId().equals(subjectID)) {
                subject = subj;
            }
        }

        for (Lecture lecture : d.getLectures()) {
            if (lecture.getId().equals(lectureID)) {
                l = lecture;
            }
        }

        for (Student student : l.getDept().getStudents()) {
            if (student.getId().equals(studentID)) {
                s = student;
            }
        }

        Grade grade = Grade
                .builder()
                .value(value)
                .type(type)
                .subject(subject)
                .lecture(l)
                .student(s)
                .createdAt(new Date(System.currentTimeMillis()))
                .createdBy(teacher)
                .build();

        repository.save(grade);
        teacher.getGrades().add(grade);
        teacherRepository.save(teacher);
        s.getGrades().add(grade);
        studentRepository.save(s);

        Email email = Email
                .builder()
                .to(s.getParent().getEmail())
                .subject(String.format("%s %s - New Grade", s.getName(), s.getSurname()))
                .text(String.format("Subject: %s\nGrade: %d\nType: %s\nGiven by: %s %s\nDate: %s", grade.getSubject().getName(), grade.getValue(), grade.getType().name(), grade.getCreatedBy().getName(), grade.getCreatedBy().getSurname(), grade.getCreatedAt().toString()))
                .build();

        emailService.sendEmail(email);
        logger.info(String.format("%s %s added a grade %d to student %s %s of type %s in %s", teacher.getName(), teacher.getSurname(), grade.getValue(), grade.getStudent().getName(), grade.getStudent().getSurname(), grade.getType().name(), l.getSubject().getName()));

        return String.format("Student %s %s received a grade %d.", s.getName(), s.getSurname(), grade.getValue());
    }

    public String updateLectureStudentGrade(Long teacherID, Long deptID, Long subjectID, Long lectureID, Long studentID, Long gradeID, int value) {
        Teacher teacher = teacherRepository.findById(teacherID).orElseThrow(() -> new UsernameNotFoundException("Teacher not found."));
        Department department = null;
        Subject subject = null;
        Lecture lecture = null;
        Student student = null;
        Grade grade = null;

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
            if (l.getId().equals(lectureID)) {
                lecture = l;
            }
        }

        for (Student s : lecture.getDept().getStudents()) {
            if (s.getId().equals(studentID)) {
                student = s;
            }
        }

        for (Grade g : student.getGrades()) {
            if (g.getId().equals(gradeID)) {
                grade = g;
            }
        }

        grade.setValue(value);
        grade.setUpdatedAt(new Date(System.currentTimeMillis()));
        grade.setUpdatedBy(teacher.getUser());
        repository.save(grade);

        Email email = Email
                .builder()
                .to(grade.getStudent().getParent().getEmail())
                .subject(String.format("%s %s - Grade Update", grade.getStudent().getName(), grade.getStudent().getSurname()))
                .text(String.format("%s %s's grade was updated to %d by %s.", grade.getStudent().getName(), grade.getStudent().getSurname(), grade.getValue(), grade.getUpdatedBy().getUsername()))
                .build();
        emailService.sendEmail(email);

        logger.info(String.format("%s updated %s %s's grade to %d.", grade.getUpdatedBy().getUsername(), grade.getStudent().getName(), grade.getStudent().getSurname(), grade.getValue()));

        return String.format("%s updated %s %s's grade to %d.", grade.getUpdatedBy().getUsername(), grade.getStudent().getName(), grade.getStudent().getSurname(), grade.getValue());
    }

    public String deleteLectureStudentGrade(Long teacherID, Long deptID, Long subjectID, Long lectureID, Long studentID, Long gradeID) {
        Teacher teacher = teacherRepository.findById(teacherID).orElseThrow(() -> new UsernameNotFoundException("Teacher not found."));
        Department department = null;
        Subject subject = null;
        Lecture lecture = null;
        Student student = null;
        Grade grade = null;

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
            if (l.getId().equals(lectureID)) {
                lecture = l;
            }
        }

        for (Student s : lecture.getDept().getStudents()) {
            if (s.getId().equals(studentID)) {
                student = s;
            }
        }

        for (Grade g : student.getGrades()) {
            if (g.getId().equals(gradeID)) {
                grade = g;
            }
        }

        Email email = Email
                .builder()
                .to(grade.getStudent().getParent().getEmail())
                .subject(String.format("%s %s - Grade Removal", grade.getStudent().getName(), grade.getStudent().getSurname()))
                .text(String.format("%s %s removed %s %s's grade %d from %s.\nDate: %s", teacher.getName(), teacher.getSurname(), grade.getStudent().getName(), grade.getStudent().getSurname(), grade.getValue(), grade.getSubject().getName(), new Date(System.currentTimeMillis()).toString()))
                .build();
        emailService.sendEmail(email);

        logger.info(String.format("%s %s removed %s %s's grade %d from %s.", teacher.getName(), teacher.getSurname(), student.getName(), student.getSurname(), grade.getValue(), grade.getSubject().getName()));

        repository.delete(grade);

        return String.format("%s %s removed %s %s's grade %d from %s.", teacher.getName(), teacher.getSurname(), student.getName(), student.getSurname(), grade.getValue(), grade.getSubject().getName());
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
}
