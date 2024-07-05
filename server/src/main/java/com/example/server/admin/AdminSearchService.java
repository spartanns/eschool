package com.example.server.admin;

import com.example.server.admin.department.Department;
import com.example.server.admin.department.DepartmentRepository;
import com.example.server.admin.department.dao.SimpleDeptView;
import com.example.server.management.feedback.FeedbackRepository;
import com.example.server.management.grade.Grade;
import com.example.server.management.grade.GradeRepository;
import com.example.server.management.grade.dao.LectureGradeView;
import com.example.server.management.lecture.Lecture;
import com.example.server.management.lecture.LectureRepository;
import com.example.server.management.subject.Subject;
import com.example.server.management.subject.SubjectRepository;
import com.example.server.management.subject.dao.LectureSubjectView;
import com.example.server.user.AdminUserView;
import com.example.server.user.User;
import com.example.server.user.UserRepository;
import com.example.server.user.parent.Parent;
import com.example.server.user.parent.ParentRepository;
import com.example.server.user.parent.dao.SimpleParentView;
import com.example.server.user.student.Student;
import com.example.server.user.student.StudentRepository;
import com.example.server.user.student.dao.AdminStudentView;
import com.example.server.user.student.dao.GradeStudentView;
import com.example.server.user.teacher.Teacher;
import com.example.server.user.teacher.TeacherRepository;
import com.example.server.user.teacher.dao.GradeTeacherView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service @RequiredArgsConstructor
public class AdminSearchService {
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final GradeRepository gradeRepository;
    private final FeedbackRepository feedbackRepository;
    private final DepartmentRepository departmentRepository;
    private final ParentRepository parentRepository;
    private final LectureRepository lectureRepository;
    private final SubjectRepository subjectRepository;

    public <T> List<T> search(String entity, String filterBy, String query) {
        List results = new ArrayList();

       switch (entity) {
           case "user":

               switch (filterBy) {
                   case "username":
                       for (User u : userRepository.findAll()) {
                           if (u.getUsername().toLowerCase().startsWith(query.toLowerCase())) {
                              AdminUserView user = AdminUserView
                                      .builder()
                                      .id(u.getId())
                                      .username(u.getUsername())
                                      .role(u.getRole())
                                      .build();
                              results.add(user);
                           }
                       }

                        break;
                   default:
                       break;
               }

               break;
           case "student":

               switch (filterBy) {
                   case "name":
                       for (Student s : studentRepository.findAll()) {
                           if (s.getName().toLowerCase().startsWith(query.toLowerCase()) || s.getSurname().toLowerCase().startsWith(query.toLowerCase())) {
                               GradeStudentView student = GradeStudentView
                                       .builder()
                                       .id(s.getId())
                                       .name(s.getName())
                                       .surname(s.getSurname())
                                       .build();
                               results.add(student);
                           }
                       }

                       break;
                   case "dept":
                       for (Student s : studentRepository.findAll()) {
                           if (s.getDept().getName().toLowerCase().startsWith(query.toLowerCase())) {
                               GradeStudentView student = GradeStudentView
                                       .builder()
                                       .id(s.getId())
                                       .name(s.getName())
                                       .surname(s.getSurname())
                                       .build();
                               results.add(student);
                           }
                       }

                       break;
                   default:
                       break;
               }

               break;
           case "parent":

               switch (filterBy) {
                   case "name":
                       for (Parent p : parentRepository.findAll()) {
                           if (p.getName().toLowerCase().startsWith(query.toLowerCase()) || p.getSurname().toLowerCase().startsWith(query.toLowerCase())) {
                               SimpleParentView parent = SimpleParentView
                                       .builder()
                                       .id(p.getId())
                                       .email(p.getEmail())
                                       .name(p.getName())
                                       .surname(p.getSurname())
                                       .build();
                               results.add(parent);
                           }
                       }

                       break;
                   case "email":
                       for (Parent p : parentRepository.findAll()) {
                           if (p.getEmail().toLowerCase().startsWith(query.toLowerCase())) {
                               SimpleParentView parent = SimpleParentView
                                       .builder()
                                       .id(p.getId())
                                       .name(p.getName())
                                       .surname(p.getSurname())
                                       .email(p.getEmail())
                                       .build();
                               results.add(parent);
                           }
                       }
                   default:
                       break;
               }
               break;
           case "teacher":

               switch (filterBy) {

                   case "name":
                       for (Teacher t : teacherRepository.findAll()) {
                           if (t.getName().toLowerCase().startsWith(query.toLowerCase()) || t.getSurname().toLowerCase().startsWith(query.toLowerCase())) {
                               GradeTeacherView teacher = GradeTeacherView
                                       .builder()
                                       .teacherID(t.getId())
                                       .name(t.getName())
                                       .surname(t.getSurname())
                                       .build();
                               results.add(teacher);
                           }
                       }
                       break;
                   case "dept":
                       for (Teacher t : teacherRepository.findAll()) {
                           for (Department d : t.getDepartments()) {
                               if (d.getName().toLowerCase().startsWith(query.toLowerCase())) {
                                   GradeTeacherView teacher = GradeTeacherView
                                           .builder()
                                           .teacherID(t.getId())
                                           .name(t.getName())
                                           .surname(t.getSurname())
                                           .build();
                                   results.add(teacher);
                               }
                           }
                       }
                       break;
                   default:
                       break;
               }
               break;
           case "dept":
               switch (filterBy) {
                   case "name":
                       for (Department d : departmentRepository.findAll()) {
                           if (d.getName().toLowerCase().startsWith(query.toLowerCase())) {
                               SimpleDeptView dept = SimpleDeptView
                                       .builder()
                                       .id(d.getId())
                                       .name(d.getName())
                                       .students(d.getStudents().size())
                                       .teachers(d.getTeachers().size())
                                       .subjects(d.getSubjects().size())
                                       .build();
                               results.add(dept);
                           }
                       }
                       break;
                   default:
                       break;
               }
               break;
           case "subject":
               switch (filterBy) {
                   case "name":
                      for (Subject s : subjectRepository.findAll()) {
                          if (s.getName().toLowerCase().startsWith(query.toLowerCase())) {
                              LectureSubjectView subject = LectureSubjectView
                                      .builder()
                                      .id(s.getId())
                                      .name(s.getName())
                                      .semester(s.getSemester())
                                      .hours(s.getHours())
                                      .build();
                              results.add(subject);
                          }
                      }
                      break;
                   case "semester":
                       for (Subject s : subjectRepository.findAll()) {
                           if (s.getSemester().name().toLowerCase().startsWith(query.toLowerCase())) {
                               LectureSubjectView subject = LectureSubjectView
                                       .builder()
                                       .id(s.getId())
                                       .name(s.getName())
                                       .semester(s.getSemester())
                                       .hours(s.getHours())
                                       .build();
                               results.add(subject);
                           }
                       }
                       break;
                   default:
                       break;
               }
               break;
           case "grade":
               switch (filterBy) {
                   case "value":
                       for (Grade g : gradeRepository.findAll()) {
                           if (g.getValue() == Integer.parseInt(query)) {
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
                                       .type(g.getType())
                                       .value(g.getValue())
                                       .student(student)
                                       .build();
                               results.add(grade);
                           }
                       }
                       break;
                   case "semester":
                       for (Grade g : gradeRepository.findAll()) {
                           if (g.getSubject().getSemester().name().toLowerCase().startsWith(query.toLowerCase())) {
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
                                       .type(g.getType())
                                       .value(g.getValue())
                                       .student(student)
                                       .build();
                               results.add(grade);
                           }
                       }
                       break;
                   case "subject":
                       for (Grade g : gradeRepository.findAll()) {
                           if (g.getSubject().getName().toLowerCase().startsWith(query.toLowerCase())) {
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
                                       .type(g.getType())
                                       .value(g.getValue())
                                       .student(student)
                                       .build();
                               results.add(grade);
                           }
                       }
                       break;
                   case "date":
                       for (Grade g : gradeRepository.findAll()) {
                           if (g.getCreatedAt().toString().startsWith(query)) {
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
                                       .type(g.getType())
                                       .value(g.getValue())
                                       .student(student)
                                       .build();
                               results.add(grade);
                           }
                       }
                       break;
                   case "student":
                       for (Grade g : gradeRepository.findAll()) {
                           if (g.getStudent().getName().toLowerCase().startsWith(query.toLowerCase()) || g.getStudent().getSurname().toLowerCase().startsWith(query.toLowerCase())) {
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
                                       .type(g.getType())
                                       .value(g.getValue())
                                       .student(student)
                                       .build();
                               results.add(grade);
                           }
                       }
                       break;
                   case "teacher":
                       for (Grade g : gradeRepository.findAll()) {
                           if (g.getCreatedBy().getName().toLowerCase().startsWith(query.toLowerCase()) || g.getCreatedBy().getSurname().startsWith(query.toLowerCase())) {
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
                                       .type(g.getType())
                                       .value(g.getValue())
                                       .student(student)
                                       .build();
                               results.add(grade);
                           }
                       }
                       break;
                   case "type":
                       for (Grade g : gradeRepository.findAll()) {
                           if (g.getType().name().toLowerCase().startsWith(query.toLowerCase())) {
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
                                       .type(g.getType())
                                       .value(g.getValue())
                                       .student(student)
                                       .build();
                               results.add(grade);
                           }
                       }
                       break;
                   default:
                       break;
               }
           default:
               break;
       }

       return results;
    }
}
