package com.example.server.user.teacher;

import com.example.server.management.department.Department;
import com.example.server.management.department.DepartmentRepository;
import com.example.server.management.grade.Grade;
import com.example.server.management.grade.GradeRepository;
import com.example.server.management.grade.Type;
import com.example.server.management.lecture.Lecture;
import com.example.server.management.lecture.LectureRepository;
import com.example.server.management.lecture.dao.LectureView;
import com.example.server.management.subject.Subject;
import com.example.server.user.User;
import com.example.server.user.UserRepository;
import com.example.server.user.email.Email;
import com.example.server.user.email.EmailService;
import com.example.server.user.student.Student;
import com.example.server.user.student.StudentRepository;
import com.example.server.user.teacher.dto.TeacherRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service @RequiredArgsConstructor
public class TeacherService {
    private final TeacherRepository repository;
    private final UserRepository userRepository;
    private final DepartmentRepository deptRepository;
    private final LectureRepository lectureRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final GradeRepository gradeRepository;
    private final EmailService emailService;
    private final PasswordEncoder encoder;

    public Teacher createTeacher(TeacherRequest request) {
        var user = User
                .builder()
                .username(request.getUsername())
                .password(encoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();
        userRepository.save(user);

        var teacher = Teacher
                .builder()
                .name(request.getName())
                .surname(request.getSurname())
                .user(user)
                .build();

        return repository.save(teacher);
    }

    public List<Teacher> index() {
        return repository.findAll();
    }

    public Teacher readTeacher(Long id) {
        return repository.findById(id).orElseThrow(() -> new UsernameNotFoundException("Teacher not found!"));
    }

    public List<Department> readDepartments(Long id) {
        Teacher teacher = repository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found."));

        return teacher.getDepartments();
    }

    public Department readDepartment(Long teacherID, Long deptID) {
        Department dept = deptRepository.findById(deptID).orElseThrow(() -> new UsernameNotFoundException("Department not found."));
        List<Teacher> teachers = dept.getTeachers();

        for (Teacher teacher : teachers) {
            if (teacher.getId().equals(teacherID)) {
                return dept;
            }
        }

        return null;
    }

    public Lecture createLecture(Long teacherID, Long deptID, Long subjectID) {
        Teacher teacher = repository.findById(teacherID).orElseThrow(() -> new UsernameNotFoundException("Teacher not found."));
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

        return lectureRepository.save(lecture);
    }

    public List<Lecture> readLectures(Long teacherID, Long deptID) {
        Teacher teacher = repository.findById(teacherID).orElseThrow(() -> new UsernameNotFoundException("Teacher not found."));
        List<Department> departments = teacher.getDepartments();
        Department dept = null;

        for (Department department : departments) {
            if (department.getId().equals(deptID)) {
                dept = department;

                break;
            }
        }


        return dept.getLectures();
    }

    public LectureView singleLecture(Long teacherID, Long deptID, Long lectureID) {
        Teacher teacher = repository.findById(teacherID).orElseThrow(() -> new UsernameNotFoundException("Teacher not found."));
        List<Department> departments = teacher.getDepartments();
        Department dept = null;
        LectureView lv = null;

        for (Department department : departments) {
            if (department.getId().equals(deptID)) {
                dept = department;

                break;
            }
        }

        List<Lecture> lectures = dept.getLectures();

        for (Lecture lecture : lectures) {
            if (lecture.getId().equals(lectureID)) {
                lv = LectureView
                        .builder()
                        .teacher(lecture.getTeacher())
                        .subject(lecture.getSubject())
                        .dept(lecture.getDept())
                        .build();
            }
        }

        return lv;
    }

    public List<Student> readLectureStudents(Long teacherID, Long deptID, Long lectureID) {
        Teacher teacher = repository.findById(teacherID).orElseThrow(() -> new UsernameNotFoundException("Teacher not found."));
        Lecture lecture = null;

        for (Lecture lecture1 : teacher.getLectures()) {
            if (lecture1.getId().equals(lectureID)) {
                lecture = lecture1;
            }
        }

        List<Student> students = lecture.getDept().getStudents();

        return students;
    }

    public Student readLectureStudent(Long teacherID, Long deptID, Long lectureID, Long studentID) {
        Teacher teacher = repository.findById(teacherID).orElseThrow(() -> new UsernameNotFoundException("Teacher not found."));
        Department dept = null;
        Lecture l = null;
        Student s = null;

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

        return s;
    }

    public List<Grade> readLectureStudentGrades(Long teacherID, Long deptID, Long lectureID, Long studentID) {
        Teacher teacher = repository.findById(teacherID).orElseThrow(() -> new UsernameNotFoundException("Teacher not found."));
        Department dept = null;
        Lecture l = null;
        Student s = null;

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

        return s.getGrades();
    }

    public String updateLectureStudentGrades(Long teacherID, Long deptID, Long lectureID, Long studentID, int value, Type type) {
        Teacher teacher = repository.findById(teacherID).orElseThrow(() -> new UsernameNotFoundException("Teacher not found."));
        Department d = null;
        Lecture l = null;
        Student s = null;

        for (Department dept : teacher.getDepartments()) {
            if (dept.getId().equals(deptID)) {
                d = dept;
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
                .lecture(l)
                .student(s)
                .createdAt(new Date(System.currentTimeMillis()))
                .createdBy(teacher)
                .build();
        gradeRepository.save(grade);
        teacher.getGrades().add(grade);
        teacherRepository.save(teacher);
        s.getGrades().add(grade);
        studentRepository.save(s);

        Email email = Email
                .builder()
                .to(s.getParent().getEmail())
                .subject(String.format("%s %s - New Grade", s.getName(), s.getSurname()))
                .text(String.format("%s %s received a grade %d of type %s in %s.", s.getName(), s.getSurname(), grade.getValue(), grade.getType().toString(), l.getSubject().getName()))
                .build();

        emailService.sendEmail(email);

        return String.format("Student %s %s received a grade %d.", s.getName(), s.getSurname(), grade.getValue());
    }
}
