package com.example.server.management;

import com.example.server.admin.department.dao.SingleDeptView;
import com.example.server.config.JwtService;
import com.example.server.admin.department.Department;
import com.example.server.admin.department.DepartmentRepository;
import com.example.server.management.grade.Grade;
import com.example.server.management.grade.GradeService;
import com.example.server.management.grade.Type;
import com.example.server.management.grade.dao.GradeView;
import com.example.server.management.lecture.Lecture;
import com.example.server.management.lecture.dao.LectureView;
import com.example.server.management.subject.Subject;
import com.example.server.management.subject.dao.SingleSubjectView;
import com.example.server.management.subject.dto.SubjectRequest;
import com.example.server.security.Views;
import com.example.server.user.User;
import com.example.server.user.UserService;
import com.example.server.user.student.Student;
import com.example.server.user.student.dao.TeacherStudentView;
import com.example.server.user.teacher.Teacher;
import com.example.server.user.teacher.TeacherService;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController @RequiredArgsConstructor
@PreAuthorize("hasRole('MANAGER')") @RequestMapping("/api/v1/mgmt")
public class TeacherController {
    private final TeacherService service;
    private final GradeService gradeService;
    private final UserService userService;
    private final DepartmentRepository deptRepository;
    private final JwtService jwtService;

    @GetMapping("/{id}") @PreAuthorize("hasAuthority('manager:read')") @JsonView(Views.Public.class)
    ResponseEntity<?> singleTeacher(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        try {
            User user = userService.readUserByUsername(jwtService.extractUsername(token.substring(7)));

            if (user.getUsername().equals(service.readTeacher(id).getUser().getUsername())) {

                return new ResponseEntity<Teacher>(service.readTeacher(id), HttpStatus.OK);
            }

            return new ResponseEntity<String>("[UNAUTHORIZED]", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {

            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/departments") @PreAuthorize("hasAuthority('manager:read')") @JsonView(Views.Public.class)
    ResponseEntity<?> viewDepartments(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        try {
            User user = userService.readUserByUsername(jwtService.extractUsername(token.substring(7)));

            if (user.getUsername().equals(service.readTeacher(id).getUser().getUsername())) {

                return new ResponseEntity<List<SingleDeptView>>(service.readDepartments(id), HttpStatus.OK);
            }

            return new ResponseEntity<String>("[UNAUTHORIZED]", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{teacherID}/departments/{deptID}") @PreAuthorize("hasAuthority('manager:read')") @JsonView(Views.General.class)
    ResponseEntity<?> viewDepartment(@RequestHeader("Authorization") String token, @PathVariable Long teacherID, @PathVariable Long deptID) {
        try {
            User user = userService.readUserByUsername(jwtService.extractUsername(token.substring(7)));
            // TODO: service??
            Department dept = deptRepository.findById(deptID) .orElseThrow(() -> new Exception("Dept not found."));
            Teacher t = null;
            Department d = null;

            for (Teacher teacher : dept.getTeachers()) {
                if (teacher.getId().equals(teacherID)) {
                    t = teacher;
                }
            }

            for (Department deptm : t.getDepartments()) {
                if (deptm.getId().equals(deptID)) {
                    d = deptm;
                }
            }

            if (user.getUsername().equals(service.readTeacher(teacherID).getUser().getUsername()) && d.getId().equals(deptID)) {

                return new ResponseEntity<SingleDeptView>(service.readDepartment(teacherID, deptID), HttpStatus.OK);
            }

            return new ResponseEntity<String>("[UNAUTHORIZED]", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{teacherID}/departments/{deptID}/subjects") @PreAuthorize("hasAuthority('manager:read')") @JsonView(Views.Public.class)
    ResponseEntity<?> getDepartmentSubjects(@RequestHeader("Authorization") String token, @PathVariable Long teacherID, @PathVariable Long deptID) {
        try {
            User user = userService.readUserByUsername(jwtService.extractUsername(token.substring(7)));
            Teacher teacher = service.readTeacher(teacherID);

            if (user.getUsername().equals(teacher.getUser().getUsername())) {
                return new ResponseEntity<List<SingleSubjectView>>(service.readDeptSubjects(teacherID, deptID), HttpStatus.OK);
            }

            return new ResponseEntity<String>("[UNAUTHORIZED]", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // TODO: Move to admin
    @PostMapping("/{teacherID}/departments/{deptID}/subjects/new") @PreAuthorize("hasAuthority('manager:create')")
    ResponseEntity<String> addDeptSubject(@PathVariable Long teacherID, @PathVariable Long deptID, @Valid @RequestBody SubjectRequest request) {
        try {
            return new ResponseEntity<String>(service.createSubject(teacherID, deptID, request), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{teacherID}/departments/{deptID}/subjects/{subjectID}") @PreAuthorize("hasAuthority('manager:read')") @JsonView(Views.Public.class)
    ResponseEntity<?> singleSubject(@RequestHeader("Authorization") String token, @PathVariable Long teacherID, @PathVariable Long deptID, @PathVariable Long subjectID) {
        try {
            User user = userService.readUserByUsername(jwtService.extractUsername(token.substring(7)));
            Teacher teacher = service.readTeacher(teacherID);

            if (user.getUsername().equals(teacher.getUser().getUsername())) {

                return new ResponseEntity<Subject>(service.readSubject(teacherID, deptID, subjectID), HttpStatus.OK);
            }

            return new ResponseEntity<String>("[UNAUTHORIZED]", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{teacherID}/departments/{deptID}/subjects/{subjectID}/lectures") @PreAuthorize("hasAuthority('manager:read')") @JsonView(Views.Public.class)
    ResponseEntity<?> getSubjectLectures(@RequestHeader("Authorization") String token, @PathVariable Long teacherID, @PathVariable Long deptID, @PathVariable Long subjectID) {
        try {
            User user = userService.readUserByUsername(jwtService.extractUsername(token.substring(7)));
            Teacher teacher = service.readTeacher(teacherID);

            if (user.getUsername().equals(teacher.getUser().getUsername())) {
                return new ResponseEntity<List<LectureView>>(service.readSubjectLectures(teacherID, deptID, subjectID), HttpStatus.OK);
            }

            return new ResponseEntity<String>("[UNAUTHORIZED]", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{teacherID}/departments/{deptID}/subjects/{subjectID}/lectures/{lectureID}") @PreAuthorize("hasAuthority('manager:read')") @JsonView(Views.General.class)
    ResponseEntity<?> singleLecture(@RequestHeader("Authorization") String token, @PathVariable Long teacherID, @PathVariable Long deptID, @PathVariable Long subjectID, @PathVariable Long lectureID) {
        try {
            User user = userService.readUserByUsername(jwtService.extractUsername(token.substring(7)));
            Teacher teacher = service.readTeacher(teacherID);

            if (user.getUsername().equals(teacher.getUser().getUsername())) {
                return new ResponseEntity<LectureView>(service.readLecture(lectureID), HttpStatus.OK);
            }

            return new ResponseEntity<String>("[UNAUTHORIZED]", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{teacherID}/departments/{deptID}/subjects/{subjectID}/lectures/{lectureID}/students") @PreAuthorize("hasAuthority('manager:read')") @JsonView(Views.Public.class)
    ResponseEntity<?> getLectureStudents(@RequestHeader("Authorization") String token, @PathVariable Long teacherID, @PathVariable Long deptID, @PathVariable Long subjectID, @PathVariable Long lectureID) {
        try {
            User user = userService.readUserByUsername(jwtService.extractUsername(token.substring(7)));
            Teacher teacher = service.readTeacher(teacherID);

            if (user.getUsername().equals(teacher.getUser().getUsername())) {
                return new ResponseEntity<List<Student>>(service.readLectureStudents(teacherID, deptID, subjectID, lectureID), HttpStatus.OK);
            }

            return new ResponseEntity<String>("[UNAUTHORIZED]", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{teacherID}/departments/{deptID}/subjects/{subjectID}/lectures/{lectureID}/students/{studentID}") @PreAuthorize("hasAuthority('manager:read')") @JsonView(Views.Public.class)
    ResponseEntity<?> getSingleLectureStudent(@RequestHeader("Authorization") String token, @PathVariable Long teacherID, @PathVariable Long deptID, @PathVariable Long subjectID, @PathVariable Long lectureID, @PathVariable Long studentID) {
       try {
           User user = userService.readUserByUsername(jwtService.extractUsername(token.substring(7)));
           Teacher teacher = service.readTeacher(teacherID);

           if (user.getUsername().equals(teacher.getUser().getUsername())) {
               return new ResponseEntity<TeacherStudentView>(service.readLectureStudent(teacherID, deptID, subjectID, lectureID, studentID), HttpStatus.OK);
           }

           return new ResponseEntity<String>("[UNAUTHORIZED]", HttpStatus.UNAUTHORIZED);
       } catch (Exception e) {
           return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
       }
    }

    @GetMapping("/{teacherID}/departments/{deptID}/subjects/{subjectID}/lectures/{lectureID}/students/{studentID}/grades") @PreAuthorize("hasAuthority('manager:read')") @JsonView(Views.Public.class)
    ResponseEntity<?> getLectureStudentGrades(@RequestHeader("Authorization") String token, @PathVariable Long teacherID, @PathVariable Long deptID, @PathVariable Long subjectID, @PathVariable Long lectureID, @PathVariable Long studentID) {
        try {
            User user = userService.readUserByUsername(jwtService.extractUsername(token.substring(7)));
            Teacher teacher = service.readTeacher(teacherID);

            if (user.getUsername().equals(teacher.getUser().getUsername())) {
                return new ResponseEntity<List<GradeView>>(service.readLectureStudentGrades(teacherID, deptID, subjectID, lectureID, studentID), HttpStatus.OK);
            }

            return new ResponseEntity<String>("[UNAUTHORIZED]", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{teacherID}/departments/{deptID}/subjects/{subjectID}/lectures/{lectureID}/students/{studentID}/grades/{gradeID}") @PreAuthorize("hasAuthority('manager:read')") @JsonView(Views.Private.class)
    ResponseEntity<?> getLectureStudentGrade(@RequestHeader("Authorization") String token, @PathVariable Long teacherID, @PathVariable Long deptID, @PathVariable Long subjectID, @PathVariable Long lectureID, @PathVariable Long studentID, @PathVariable Long gradeID) {
        try {
            User user = userService.readUserByUsername(jwtService.extractUsername(token.substring(7)));
            Teacher teacher = service.readTeacher(teacherID);
            Grade grade = gradeService.readGrade(gradeID);

            if (user.getUsername().equals(teacher.getUser().getUsername()) && user.getUsername().equals(grade.getCreatedBy().getUser().getUsername())) {
                return new ResponseEntity<GradeView>(service.readLectureStudentGrade(teacherID, deptID, subjectID, lectureID, studentID, gradeID), HttpStatus.OK);
            }

            return new ResponseEntity<String>("[UNAUTHORIZED]", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // TODO: Move to admin
    @DeleteMapping("/{teacherID}/departments/{deptID}/subjects/{subjectID}/delete") @PreAuthorize("hasAuthority('manager:delete')")
    ResponseEntity<String> deleteDeptSubject(@PathVariable Long teacherID, @PathVariable Long deptID, @PathVariable Long subjectID) {
        try {
            return new ResponseEntity<String>(service.deleteSubject(teacherID, deptID, subjectID), HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{teacherID}/departments/{deptID}/subjects/{subjectID}/lectures/new") @PreAuthorize("hasAuthority('manager:create')")
    ResponseEntity<?> addLecture(@PathVariable Long teacherID, @PathVariable Long deptID, @PathVariable Long subjectID) {
        try {
            return new ResponseEntity<Lecture>(service.createLecture(teacherID, deptID, subjectID), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{teacherID}/departments/{deptID}/subjects/{subjectID}/lectures/{lectureID}/students/{studentID}/grades/new") @PreAuthorize("hasAuthority('manager:create')")
    ResponseEntity<?> addGradeToStudent(@RequestHeader("Authorization") String token, @PathVariable Long teacherID, @PathVariable Long deptID, @PathVariable Long subjectID, @PathVariable Long lectureID, @PathVariable Long studentID, @RequestParam int value, @RequestParam Type type) {
        try {
            User user = userService.readUserByUsername(jwtService.extractUsername(token.substring(7)));
            Teacher teacher = service.readTeacher(teacherID);

            if (user.getUsername().equals(teacher.getUser().getUsername())) {

                return new ResponseEntity<String>(service.updateLectureStudentGrades(teacherID, deptID, subjectID, lectureID, studentID, value, type), HttpStatus.CREATED);
            }

            return new ResponseEntity<String>("[UNAUTHORIZED]", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {

            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
/*
    @PatchMapping("/{teacherID}/departments/{deptID}/lectures/{lectureID}/students/{studentID}/grades/new") @PreAuthorize("hasAuthority('manager:update')")
    ResponseEntity<String> addLectureStudentGrade(@PathVariable Long teacherID, @PathVariable Long deptID, @PathVariable Long lectureID, @PathVariable Long studentID, @RequestParam int value, @RequestParam Type type) {
        try {
            return new ResponseEntity<String>(service.updateLectureStudentGrades(teacherID, deptID, lectureID, studentID, value, type), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    */
}
