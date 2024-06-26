package com.example.server.user.teacher;

import com.example.server.admin.department.Department;
import com.example.server.admin.department.DepartmentRepository;
import com.example.server.management.grade.Grade;
import com.example.server.management.grade.GradeRepository;
import com.example.server.management.grade.Type;
import com.example.server.management.grade.dao.GradeView;
import com.example.server.management.lecture.Lecture;
import com.example.server.management.lecture.LectureRepository;
import com.example.server.management.lecture.dao.LectureView;
import com.example.server.management.subject.Subject;
import com.example.server.management.subject.SubjectRepository;
import com.example.server.management.subject.dto.SubjectRequest;
import com.example.server.user.User;
import com.example.server.user.UserRepository;
import com.example.server.user.email.Email;
import com.example.server.user.email.EmailService;
import com.example.server.user.student.Student;
import com.example.server.user.student.StudentRepository;
import com.example.server.user.student.dao.TeacherStudentView;
import com.example.server.user.teacher.dao.GradeTeacherView;
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
    private final LectureRepository lectureRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final GradeRepository gradeRepository;
    private final EmailService emailService;
    private final PasswordEncoder encoder;
    private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

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

        logger.info(String.format("Teacher %s %s created.", teacher.getName(), teacher.getSurname()));

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
        Teacher teacher = repository.findById(teacherID).orElseThrow(() -> new UsernameNotFoundException("Teacher not found."));
        Department d = null;

        for (Department dept : teacher.getDepartments()) {
            if (dept.getId().equals(deptID)) {
                d = dept;
            }
        }

        return d;
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

        logger.info(String.format("Lecture with ID: %d created by %s %s.", lecture.getId(), lecture.getTeacher().getName(), lecture.getTeacher().getSurname()));

        s.setHours(s.getHours() + 1);

        subjectRepository.save(s);

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
                        .teacher(String.format("%s %s", lecture.getTeacher().getName(), lecture.getTeacher().getSurname()))
                        .subject(lecture.getSubject().getName())
                        .build();
            }
        }

        return lv;
    }

    public List<Student> readLectureStudents(Long teacherID, Long deptID, Long subjectID, Long lectureID) {
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

    public TeacherStudentView readLectureStudent(Long teacherID, Long deptID, Long subjectID, Long lectureID, Long studentID) {
        Teacher teacher = repository.findById(teacherID).orElseThrow(() -> new UsernameNotFoundException("Teacher not found."));
        Department dept = null;
        Lecture l = null;
        Student s = null;
        List<GradeView> subjectGrades = new ArrayList<>();

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

        Subject subject = l.getSubject();

        for (Student student : l.getDept().getStudents()) {
            if (student.getId().equals(studentID)) {
                s = student;
            }
        }

        for (Grade grade : s.getGrades()) {
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
                    .createdAt(grade.getCreatedAt())
                    .createdBy(teacherView)
                    .semester(grade.getSubject().getSemester())
                    .build();

            if (grade.getSubject().equals(subject)) {
                subjectGrades.add(view);
            }
        }

        TeacherStudentView view = TeacherStudentView
                .builder()
                .id(s.getId())
                .name(s.getName())
                .surname(s.getSurname())
                .grades(subjectGrades)
                .build();

        return view;
    }

    public List<GradeView> readLectureStudentGrades(Long teacherID, Long deptID, Long subjectID, Long lectureID, Long studentID) {
        Teacher teacher = repository.findById(teacherID).orElseThrow(() -> new UsernameNotFoundException("Teacher not found."));
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

    public String updateLectureStudentGrades(Long teacherID, Long deptID, Long subjectID, Long lectureID, Long studentID, int value, Type type) {
        Teacher teacher = repository.findById(teacherID).orElseThrow(() -> new UsernameNotFoundException("Teacher not found."));
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

        gradeRepository.save(grade);
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

    public List<Subject> readDeptSubjects(Long teacherID, Long deptID) {
        Teacher teacher = repository.findById(teacherID).orElseThrow(() -> new UsernameNotFoundException("Teacher not found."));
        List<Department> departments = teacher.getDepartments();
        Department d = null;

        for (Department dept : departments) {
            if (dept.getId().equals(deptID)) {
                d = dept;
            }
        }

        return d.getSubjects();
    }

    public Subject readSubject(Long teacherID, Long deptID, Long subjectID) {
        Teacher teacher = repository.findById(teacherID).orElseThrow(() -> new UsernameNotFoundException("Teacher not found."));
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

    public String deleteSubject(Long teacherID, Long deptID, Long subjectID) {
        Teacher teacher = repository.findById(teacherID).orElseThrow(() -> new UsernameNotFoundException("Teacher not found."));
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

        subjectRepository.delete(s);

        logger.warn(String.format("Subject with ID: %d deleted.", subjectID));

        return String.format("Subject with ID: %d deleted.", subjectID);
    }

    public String createSubject(Long teacherID, Long deptID, SubjectRequest request) {
        Teacher teacher = repository.findById(teacherID).orElseThrow(() -> new UsernameNotFoundException("Teacher not found."));
        Department d = null;

        for (Department dept : teacher.getDepartments()) {
            if (dept.getId().equals(deptID)) {
                d = dept;
            }
        }

        Subject subject = Subject
                .builder()
                .name(request.getName())
                .semester(request.getSemester())
                .dept(d)
                .build();

        subjectRepository.save(subject);

        d.getSubjects().add(subject);
        deptRepository.save(d);

        logger.info(String.format("Subject with ID: %d created.", subject.getId()));

        return String.format("Subject with ID: %d created.", subject.getId());
    }

    public LectureView readLecture(Long id) {
        Lecture lecture = lectureRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("Lecture not found."));
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
}
