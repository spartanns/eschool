package com.example.server.admin.department;

import com.example.server.admin.department.dao.AdminDeptView;
import com.example.server.admin.department.dao.SingleDeptView;
import com.example.server.admin.department.dto.DeptRequest;
import com.example.server.management.grade.Grade;
import com.example.server.management.grade.dao.LectureGradeView;
import com.example.server.management.lecture.Lecture;
import com.example.server.management.lecture.dao.AdminLectureView;
import com.example.server.management.lecture.dao.LectureView;
import com.example.server.management.subject.Subject;
import com.example.server.management.subject.SubjectRepository;
import com.example.server.management.subject.dao.AdminSubjectView;
import com.example.server.management.subject.dao.LectureSubjectView;
import com.example.server.management.subject.dao.SingleSubjectView;
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
import java.util.List;

@Service @RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository repository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;
    private final DepartmentRepository deptRepository;
    private Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

    public Department createDept(DeptRequest request) {
        Department dept = Department
                .builder()
                .name(request.getName())
                .build();


        repository.save(dept);

        logger.info(String.format("Department %s created.", dept.getName()));

        return dept;
    }

    public Department readDept(Long id) {
        return repository.findById(id).orElseThrow(() -> new UsernameNotFoundException("Department not found."));
    }

    public List<AdminDeptView> index() {
        List<AdminDeptView> departments = new ArrayList<>();
        List<SingleSubjectView> subjects = new ArrayList<>();
        List<AdminTeacherView> teachers = new ArrayList<>();
        List<AdminStudentView> students = new ArrayList<>();

        for (Department d : repository.findAll()) {
            for (Subject s : d.getSubjects()) {
                SingleSubjectView subject = SingleSubjectView
                        .builder()
                        .id(s.getId())
                        .name(s.getName())
                        .semester(s.getSemester())
                        .build();
                subjects.add(subject);
            }

            for (Teacher t : d.getTeachers()) {
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

            for (Student s : d.getStudents()) {
                AdminUserView user = AdminUserView
                        .builder()
                        .id(s.getUser().getId())
                        .username(s.getUser().getUsername())
                        .role(s.getUser().getRole())
                        .build();

                AdminStudentView student = AdminStudentView
                        .builder()
                        .id(s.getId())
                        .name(s.getName())
                        .surname(s.getSurname())
                        .user(user)
                        .build();
                students.add(student);
            }

            AdminDeptView dept = AdminDeptView
                    .builder()
                    .id(d.getId())
                    .name(d.getName())
                    .subjects(subjects)
                    .teachers(teachers)
                    .students(students)
                    .build();
            departments.add(dept);
        }

        return departments;
    }

    public AdminDeptView readDeptView(Long id) {
        Department d = readDept(id);
        List<SingleSubjectView> subjects = new ArrayList<>();
        List<AdminStudentView> students = new ArrayList<>();
        List<AdminTeacherView> teachers = new ArrayList<>();
        List<LectureGradeView> grades = new ArrayList<>();
        List<AdminLectureView> lectures = new ArrayList<>();
        List<AdminStudentView> attendants = new ArrayList<>();

        for (Subject s : d.getSubjects()) {
            for (Grade g : s.getGrades()) {
                AdminSubjectView subject = AdminSubjectView
                        .builder()
                        .id(g.getSubject().getId())
                        .name(g.getSubject().getName())
                        .hours(g.getSubject().getHours())
                        .semester(g.getSubject().getSemester())
                        .dept(g.getStudent().getDept().getName())
                        .lectures(s.getLectures())
                        .grades(s.getGrades())
                        .build();

                AdminUserView teacherUser = AdminUserView
                        .builder()
                        .id(g.getCreatedBy().getId())
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

                LectureGradeView grade = LectureGradeView
                        .builder()
                        .id(g.getId())
                        .value(g.getValue())
                        .type(g.getType())
                        .student(student)
                        .build();
                grades.add(grade);
            }

            for (Lecture l : s.getLectures()) {
                LectureSubjectView subject = LectureSubjectView
                        .builder()
                        .id(s.getId())
                        .name(s.getName())
                        .hours(s.getHours())
                        .semester(s.getSemester())
                        .build();

                for (Student st : l.getAttendants()) {
                    AdminUserView user = AdminUserView
                            .builder()
                            .id(st.getUser().getId())
                            .username(st.getUser().getUsername())
                            .role(st.getUser().getRole())
                            .build();

                    AdminStudentView student = AdminStudentView
                            .builder()
                            .id(st.getId())
                            .name(st.getName())
                            .surname(st.getSurname())
                            .user(user)
                            .build();
                    attendants.add(student);
                }

                AdminUserView user = AdminUserView
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
                        .user(user)
                        .build();

                AdminLectureView lecture = AdminLectureView
                        .builder()
                        .id(l.getId())
                        .subject(subject)
                        .grades(grades)
                        .feedbacks(l.getFeedbacks())
                        .teacher(teacher)
                        .attendants(attendants)
                        .createdAt(l.getCreatedAt())
                        .feedbacks(l.getFeedbacks())
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

        for (Teacher t : d.getTeachers()) {
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

        for (Student s : d.getStudents()) {
            AdminUserView user = AdminUserView
                    .builder()
                    .id(s.getUser().getId())
                    .username(s.getUser().getUsername())
                    .role(s.getUser().getRole())
                    .build();

            AdminStudentView student = AdminStudentView
                    .builder()
                    .id(s.getId())
                    .name(s.getName())
                    .surname(s.getSurname())
                    .user(user)
                    .build();
            students.add(student);
        }

        AdminDeptView dept = AdminDeptView
                .builder()
                .id(d.getId())
                .name(d.getName())
                .subjects(subjects)
                .teachers(teachers)
                .students(students)
                .grades(grades)
                .lectures(lectures)
                .build();

        return dept;
    }

    public String addStudent(Long deptID, Long studentID) {
        Department dept = repository.findById(deptID).orElseThrow(() -> new UsernameNotFoundException("Department not found."));
        Student student = studentRepository.findById(studentID).orElseThrow(() -> new UsernameNotFoundException("Student not found."));

        student.setDept(dept);
        studentRepository.save(student);

        dept.getStudents().add(student);
        repository.save(dept);

        logger.info(String.format("Student %s %s with ID: %d added to department %s", student.getName(), student.getSurname(), student.getId(), dept.getName()));

        return String.format("Student with ID: %d added to department ID: %d", studentID, deptID);
    }

    public String addSubject(Long deptID, Long subjectID) {
        Department dept = repository.findById(deptID).orElseThrow(() -> new UsernameNotFoundException("Department not found."));
        Subject subject = subjectRepository.findById(subjectID).orElseThrow(() -> new UsernameNotFoundException("Subject not found."));

        subject.setDept(dept);
        subjectRepository.save(subject);

        dept.getSubjects().add(subject);
        repository.save(dept);

        logger.info(String.format("Subject %s with ID: %d added to department %s.", subject.getName(), subject.getId(), dept.getName()));

        return String.format("Subject %s with ID: %d added to department %s.", subject.getName(), subject.getId(), dept.getName());
    }

    public String addTeacher(Long deptID, Long teacherID) {
        Teacher teacher = teacherRepository.findById(teacherID).orElseThrow(() -> new UsernameNotFoundException("Teacher not found."));
        Department dept = deptRepository.findById(deptID).orElseThrow(() -> new UsernameNotFoundException("Department not found."));

        teacher.getDepartments().add(dept);
        teacherRepository.save(teacher);

        dept.getTeachers().add(teacher);
        deptRepository.save(dept);

        logger.info(String.format("Teacher %s %s added to department %s.", teacher.getName(), teacher.getSurname(), dept.getName()));

        return String.format("Teacher %s %s added to department %s.", teacher.getName(), teacher.getSurname(), dept.getName());
    }

    public String deleteDept(Long id) {
        Department dept = repository.findById(id).orElseThrow(() -> new UsernameNotFoundException("Department not found."));
        repository.delete(dept);

        logger.warn(String.format("Department with ID: %d deleted.", id));

        return String.format("Department with ID: %d deleted.", id);
    }

    public List<SingleDeptView> readTeacherDepartments(Long id) {
        Teacher teacher = teacherRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found."));
        List<SingleDeptView> depts = new ArrayList<>();
        List<SingleSubjectView> subjects = new ArrayList<>();
        List<LectureView> lectures = new ArrayList<>();
        List<GradeStudentView> students = new ArrayList<>();

        for (Department d : teacher.getDepartments()) {
            for (Subject subject : d.getSubjects()) {
                for (Lecture lecture : subject.getLectures()) {
                    for (Student s : lecture.getAttendants()) {
                        GradeStudentView student = GradeStudentView
                                .builder()
                                .id(s.getId())
                                .name(s.getName())
                                .surname(s.getSurname())
                                .build();
                        students.add(student);
                    }

                    LectureView l = LectureView
                            .builder()
                            .id(lecture.getId())
                            .subject(lecture.getSubject().getName())
                            .teacher(String.format("%s %s", lecture.getTeacher().getName(), lecture.getTeacher().getSurname()))
                            .attendants(students)
                            .build();
                    lectures.add(l);
                }

                SingleSubjectView s = SingleSubjectView
                        .builder()
                        .id(subject.getId())
                        .name(subject.getName())
                        .build();
                subjects.add(s);

            }

            SingleDeptView dept = SingleDeptView
                    .builder()
                    .deptID(d.getId())
                    .name(d.getName())
                    .subjects(subjects)
                    .build();
            depts.add(dept);
        }

        return depts;
    }

    public SingleDeptView readTeacherDepartment(Long teacherID, Long deptID) {
        Teacher teacher = teacherRepository.findById(teacherID).orElseThrow(() -> new UsernameNotFoundException("Teacher not found."));
        List<SingleSubjectView> subjects = new ArrayList<>();
        List<LectureView> lectures = new ArrayList<>();
        List<GradeStudentView> students = new ArrayList<>();

        // TODO: Nesting

        for (Department d : teacher.getDepartments()) {
            if (d.getId().equals(deptID)) {
                for (Student s : d.getStudents()) {
                    GradeStudentView student = GradeStudentView
                            .builder()
                            .id(s.getId())
                            .name(s.getName())
                            .surname(s.getSurname())
                            .build();
                    students.add(student);
                }

                for (Subject s : d.getSubjects()) {
                    for (Lecture l : s.getLectures()) {
                        LectureView lecture = LectureView
                                .builder()
                                .id(l.getId())
                                .date(l.getCreatedAt())
                                .attendants(students)
                                .teacher(String.format("%s %s", l.getTeacher().getName(), l.getTeacher().getSurname()))
                                .subject(l.getSubject().getName())
                                .build();
                        lectures.add(lecture);
                    }

                    SingleSubjectView subject = SingleSubjectView
                            .builder()
                            .id(s.getId())
                            .semester(s.getSemester())
                            .name(s.getName())
                            .build();
                    subjects.add(subject);
                }

                SingleDeptView dept = SingleDeptView
                        .builder()
                        .deptID(d.getId())
                        .name(d.getName())
                        .subjects(subjects)
                        .build();

                return dept;
            }
        }

        throw new UsernameNotFoundException("Dept not found.");
    }
}
