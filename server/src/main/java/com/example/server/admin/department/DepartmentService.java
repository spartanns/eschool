package com.example.server.admin.department;

import com.example.server.admin.department.dao.SingleDeptView;
import com.example.server.admin.department.dto.DeptRequest;
import com.example.server.management.lecture.Lecture;
import com.example.server.management.lecture.dao.LectureView;
import com.example.server.management.subject.Subject;
import com.example.server.management.subject.SubjectRepository;
import com.example.server.management.subject.dao.SingleSubjectView;
import com.example.server.user.student.Student;
import com.example.server.user.student.StudentRepository;
import com.example.server.user.student.dao.GradeStudentView;
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

    public List<Department> index() {
        return repository.findAll();
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
                        .lectures(lectures)
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
                            .lectures(lectures)
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
