package com.example.server.management;

import com.example.server.admin.department.DepartmentService;
import com.example.server.admin.department.dao.SingleDeptView;
import com.example.server.config.JwtService;
import com.example.server.management.feedback.FeedbackService;
import com.example.server.management.feedback.dao.TeacherFeedbackView;
import com.example.server.management.feedback.dto.FeedbackRequest;
import com.example.server.management.grade.Grade;
import com.example.server.management.grade.GradeService;
import com.example.server.management.grade.Type;
import com.example.server.management.grade.dao.GradeView;
import com.example.server.management.grade.dao.TeacherGradeView;
import com.example.server.management.lecture.Lecture;
import com.example.server.management.lecture.LectureService;
import com.example.server.management.lecture.dao.LectureView;
import com.example.server.management.lecture.dao.TeacherLectureView;
import com.example.server.management.subject.Subject;
import com.example.server.management.subject.SubjectService;
import com.example.server.management.subject.dao.SingleSubjectView;
import com.example.server.security.Views;
import com.example.server.user.User;
import com.example.server.user.UserService;
import com.example.server.user.student.StudentService;
import com.example.server.user.student.dao.TeacherStudentView;
import com.example.server.user.teacher.Teacher;
import com.example.server.user.teacher.TeacherService;
import com.example.server.user.teacher.dao.TeacherView;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController @RequiredArgsConstructor
@PreAuthorize("hasRole('MANAGER')") @RequestMapping("/api/v1/mgmt")
public class TeacherController {
    private final TeacherService service;
    private final GradeService gradeService;
    private final UserService userService;
    private final SubjectService subjectService;
    private final DepartmentService deptService;
    private final LectureService lectureService;
    private final StudentService studentService;
    private final FeedbackService feedbackService;
    private final JwtService jwtService;

    @GetMapping("/{id}") @PreAuthorize("hasAuthority('manager:read')") @JsonView(Views.Public.class)
    ResponseEntity<?> singleTeacher(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        try {
            User user = userService.readUserByUsername(jwtService.extractUsername(token.substring(7)));

            if (user.getUsername().equals(service.readTeacher(id).getUser().getUsername())) {

                return new ResponseEntity<TeacherView>(service.readTeacherView(id), HttpStatus.OK);
            }

            return new ResponseEntity<String>("[UNAUTHORIZED]", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {

            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}/departments") @PreAuthorize("hasAuthority('manager:read')") @JsonView(Views.Public.class)
    ResponseEntity<?> viewDepartments(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        try {
            User user = userService.readUserByUsername(jwtService.extractUsername(token.substring(7)));

            if (user.getUsername().equals(service.readTeacher(id).getUser().getUsername())) {

                return new ResponseEntity<List<SingleDeptView>>(deptService.readTeacherDepartments(id), HttpStatus.OK);
            }

            return new ResponseEntity<String>("[UNAUTHORIZED]", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{teacherID}/departments/{deptID}") @PreAuthorize("hasAuthority('manager:read')") @JsonView(Views.General.class)
    ResponseEntity<?> viewDepartment(@RequestHeader("Authorization") String token, @PathVariable Long teacherID, @PathVariable Long deptID) {
        try {
            User user = userService.readUserByUsername(jwtService.extractUsername(token.substring(7)));
            Teacher teacher = service.readTeacher(teacherID);

            if (user.getUsername().equals(teacher.getUser().getUsername())) {

                return new ResponseEntity<SingleDeptView>(deptService.readTeacherDepartment(teacherID, deptID), HttpStatus.OK);
            }

            return new ResponseEntity<String>("[UNAUTHORIZED]", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{teacherID}/departments/{deptID}/subjects") @PreAuthorize("hasAuthority('manager:read')") @JsonView(Views.Public.class)
    ResponseEntity<?> getDepartmentSubjects(@RequestHeader("Authorization") String token, @PathVariable Long teacherID, @PathVariable Long deptID) {
        try {
            User user = userService.readUserByUsername(jwtService.extractUsername(token.substring(7)));
            Teacher teacher = service.readTeacher(teacherID);

            if (user.getUsername().equals(teacher.getUser().getUsername())) {
                return new ResponseEntity<List<SingleSubjectView>>(subjectService.readTeacherDeptSubjects(teacherID, deptID), HttpStatus.OK);
            }

            return new ResponseEntity<String>("[UNAUTHORIZED]", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{teacherID}/departments/{deptID}/subjects/{subjectID}") @PreAuthorize("hasAuthority('manager:read')") @JsonView(Views.Private.class)
    ResponseEntity<?> singleSubject(@RequestHeader("Authorization") String token, @PathVariable Long teacherID, @PathVariable Long deptID, @PathVariable Long subjectID) {
        try {
            User user = userService.readUserByUsername(jwtService.extractUsername(token.substring(7)));
            Teacher teacher = service.readTeacher(teacherID);

            if (user.getUsername().equals(teacher.getUser().getUsername())) {

                return new ResponseEntity<SingleSubjectView>(subjectService.readTeacherDeptSubject(teacherID, deptID, subjectID), HttpStatus.OK);
            }

            return new ResponseEntity<String>("[UNAUTHORIZED]", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{teacherID}/departments/{deptID}/subjects/{subjectID}/lectures") @PreAuthorize("hasAuthority('manager:read')") @JsonView(Views.Public.class)
    ResponseEntity<?> getSubjectLectures(@RequestHeader("Authorization") String token, @PathVariable Long teacherID, @PathVariable Long deptID, @PathVariable Long subjectID) {
        try {
            User user = userService.readUserByUsername(jwtService.extractUsername(token.substring(7)));
            Teacher teacher = service.readTeacher(teacherID);

            if (user.getUsername().equals(teacher.getUser().getUsername())) {
                return new ResponseEntity<List<TeacherLectureView>>(lectureService.readSubjectLectures(teacherID, deptID, subjectID), HttpStatus.OK);
            }

            return new ResponseEntity<String>("[UNAUTHORIZED]", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{teacherID}/departments/{deptID}/subjects/{subjectID}/lectures/{lectureID}") @PreAuthorize("hasAuthority('manager:read')") @JsonView(Views.General.class)
    ResponseEntity<?> singleLecture(@RequestHeader("Authorization") String token, @PathVariable Long teacherID, @PathVariable Long deptID, @PathVariable Long subjectID, @PathVariable Long lectureID) {
        try {
            User user = userService.readUserByUsername(jwtService.extractUsername(token.substring(7)));
            Teacher teacher = service.readTeacher(teacherID);

            if (user.getUsername().equals(teacher.getUser().getUsername())) {
                return new ResponseEntity<LectureView>(lectureService.readLectureView(teacherID, deptID, subjectID, lectureID), HttpStatus.OK);
            }

            return new ResponseEntity<String>("[UNAUTHORIZED]", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{teacherID}/departments/{deptID}/subjects/{subjectID}/lectures/{lectureID}/students") @PreAuthorize("hasAuthority('manager:read')") @JsonView(Views.Public.class)
    ResponseEntity<?> getLectureStudents(@RequestHeader("Authorization") String token, @PathVariable Long teacherID, @PathVariable Long deptID, @PathVariable Long subjectID, @PathVariable Long lectureID) {
        try {
            User user = userService.readUserByUsername(jwtService.extractUsername(token.substring(7)));
            Teacher teacher = service.readTeacher(teacherID);

            if (user.getUsername().equals(teacher.getUser().getUsername())) {
                return new ResponseEntity<List<TeacherStudentView>>(studentService.readLectureStudents(teacherID, deptID, subjectID, lectureID), HttpStatus.OK);
            }

            return new ResponseEntity<String>("[UNAUTHORIZED]", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{teacherID}/departments/{deptID}/subjects/{subjectID}/lectures/{lectureID}/students/{studentID}") @PreAuthorize("hasAuthority('manager:read')") @JsonView(Views.General.class)
    ResponseEntity<?> getSingleLectureStudent(@RequestHeader("Authorization") String token, @PathVariable Long teacherID, @PathVariable Long deptID, @PathVariable Long subjectID, @PathVariable Long lectureID, @PathVariable Long studentID) {
       try {
           User user = userService.readUserByUsername(jwtService.extractUsername(token.substring(7)));
           Teacher teacher = service.readTeacher(teacherID);

           if (user.getUsername().equals(teacher.getUser().getUsername())) {
               return new ResponseEntity<TeacherStudentView>(studentService.readLectureStudent(teacherID, deptID, subjectID, lectureID, studentID), HttpStatus.OK);
           }

           return new ResponseEntity<String>("[UNAUTHORIZED]", HttpStatus.UNAUTHORIZED);
       } catch (Exception e) {
           return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
       }
    }

    @PatchMapping("/{teacherID}/departments/{deptID}/subjects/{subjectID}/lectures/{lectureID}/students/{studentID}/present") @PreAuthorize("hasAuthority('manager:update')")
    ResponseEntity<?> markStudentAsPresent(@RequestHeader("Authorization") String token, @PathVariable Long teacherID, @PathVariable Long deptID, @PathVariable Long subjectID, @PathVariable Long lectureID, @PathVariable Long studentID) {
        try {
            User user = userService.readUserByUsername(jwtService.extractUsername(token.substring(7)));
            Teacher teacher = service.readTeacher(teacherID);

            if (user.getUsername().equals(teacher.getUser().getUsername())) {
                return new ResponseEntity<String>(studentService.markStudentPresent(teacherID, deptID, subjectID, lectureID, studentID), HttpStatus.CREATED);
            }

            return new ResponseEntity<String>("[UNAUTHORIZED]", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{teacherID}/departments/{deptID}/subjects/{subjectID}/lectures/{lectureID}/students/{studentID}/missing") @PreAuthorize("hasAuthority('manager:update')")
    ResponseEntity<?> markStudentAsMissing(@RequestHeader("Authorization") String token, @PathVariable Long teacherID, @PathVariable Long deptID, @PathVariable Long subjectID, @PathVariable Long lectureID, @PathVariable Long studentID) {
        try {
            User user = userService.readUserByUsername(jwtService.extractUsername(token.substring(7)));
            Teacher teacher = service.readTeacher(teacherID);

            if (user.getUsername().equals(teacher.getUser().getUsername())) {
                return new ResponseEntity<String>(studentService.markStudentMissing(studentID), HttpStatus.CREATED);
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
                return new ResponseEntity<List<GradeView>>(gradeService.readLectureStudentGrades(teacherID, deptID, subjectID, lectureID, studentID), HttpStatus.OK);
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
                return new ResponseEntity<GradeView>(gradeService.readLectureStudentGrade(teacherID, deptID, subjectID, lectureID, studentID, gradeID), HttpStatus.OK);
            }

            return new ResponseEntity<String>("[UNAUTHORIZED]", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{teacherID}/departments/{deptID}/subjects/{subjectID}/lectures/new") @PreAuthorize("hasAuthority('manager:create')")
    ResponseEntity<?> addLecture(@PathVariable Long teacherID, @PathVariable Long deptID, @PathVariable Long subjectID) {
        try {
            return new ResponseEntity<Lecture>(lectureService.createLecture(teacherID, deptID, subjectID), HttpStatus.CREATED);
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

                return new ResponseEntity<String>(gradeService.postLectureStudentGrade(teacherID, deptID, subjectID, lectureID, studentID, value, type), HttpStatus.CREATED);
            }

            return new ResponseEntity<String>("[UNAUTHORIZED]", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {

            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{teacherID}/departments/{deptID}/subjects/{subjectID}/lectures/{lectureID}/students/{studentID}/grades/{gradeID}/update") @PreAuthorize("hasAuthority('manager:update')")
    ResponseEntity<?> editLectureStudentGrade(@RequestHeader("Authorization") String token, @PathVariable Long teacherID, @PathVariable Long deptID, @PathVariable Long subjectID, @PathVariable Long lectureID, @PathVariable Long studentID, @PathVariable Long gradeID, @RequestParam int value) {
        try {
            User user = userService.readUserByUsername(jwtService.extractUsername(token.substring(7)));
            Teacher teacher = service.readTeacher(teacherID);
            Grade grade = gradeService.readGrade(gradeID);

            if (user.getUsername().equals(teacher.getUser().getUsername()) && user.getUsername().equals(grade.getCreatedBy().getUser().getUsername())) {
                return new ResponseEntity<String>(gradeService.updateLectureStudentGrade(teacherID, deptID, subjectID, lectureID, studentID, gradeID, value), HttpStatus.CREATED);
            }

            return new ResponseEntity<String>("[UNAUHTORIZED]", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{teacherID}/departments/{deptID}/subjects/{subjectID}/lectures/{lectureID}/students/{studentID}/grades/{gradeID}/delete") @PreAuthorize("hasAuthority('manager:delete')")
    ResponseEntity<String> removeLectureStudentGrade(@RequestHeader("Authorization") String token, @PathVariable Long teacherID, @PathVariable Long deptID, @PathVariable Long subjectID, @PathVariable Long lectureID, @PathVariable Long studentID, @PathVariable Long gradeID) {
        try {
            User user = userService.readUserByUsername(jwtService.extractUsername(token.substring(7)));
            Teacher teacher = service.readTeacher(teacherID);
            Grade grade = gradeService.readGrade(gradeID);

            if (user.getUsername().equals(teacher.getUser().getUsername()) && user.getUsername().equals(grade.getCreatedBy().getUser().getUsername())) {
                return new ResponseEntity<String>(gradeService.deleteLectureStudentGrade(teacherID, deptID, subjectID, lectureID, studentID, gradeID), HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<String>("[UNAUTHORIZED]", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{teacherID}/departments/{deptID}/subjects/{subjectID}/lectures/{lectureID}/students/{studentID}/feedbacks/new") @PreAuthorize("hasAuthority('manager:create')")
    ResponseEntity<?> addStudentFeedback(@RequestHeader("Authorization") String token, @PathVariable Long teacherID, @PathVariable Long deptID, @PathVariable Long subjectID, @PathVariable Long lectureID, @PathVariable Long studentID, @Valid @RequestBody FeedbackRequest request) {
        try {
            User user = userService.readUserByUsername(jwtService.extractUsername(token.substring(7)));
            Teacher teacher = service.readTeacher(teacherID);

            if (user.getUsername().equals(teacher.getUser().getUsername())) {
                return new ResponseEntity<String>(feedbackService.createFeedback(teacherID, deptID, subjectID, lectureID, studentID, request), HttpStatus.CREATED);
            }

            return new ResponseEntity<String>("[UNAUTHORIZED]", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/grades") @PreAuthorize("hasAuthority('manager:read')") @JsonView(Views.Public.class)
    ResponseEntity<?> viewTeacherGrades(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        try {
            User user = userService.readUserByUsername(jwtService.extractUsername(token.substring(7)));
            Teacher teacher = service.readTeacher(id);

            if (user.getUsername().equals(teacher.getUser().getUsername())) {
                return new ResponseEntity<List<GradeView>>(gradeService.readTeacherGrades(id), HttpStatus.OK);
            }

            return new ResponseEntity<String>("[UNAUTHORIZED]", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{teacherID}/grades/{gradeID}") @PreAuthorize("hasAuthority('manager:read')") @JsonView(Views.General.class)
    ResponseEntity<?> viewSingleTeacherGrade(@RequestHeader("Authorization") String token, @PathVariable Long teacherID, @PathVariable Long gradeID) {
        try {
            User user = userService.readUserByUsername(jwtService.extractUsername(token.substring(7)));
            Teacher teacher = service.readTeacher(teacherID);

            if (user.getUsername().equals(teacher.getUser().getUsername())) {
                return new ResponseEntity<TeacherGradeView>(gradeService.readSingleTeacherGrade(gradeID), HttpStatus.OK);
            }

            return new ResponseEntity<String>("[UNAUTHORIZED]", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{teacherID}/grades/search") @PreAuthorize("hasAuthority('manager:read')") @JsonView(Views.Public.class)
    ResponseEntity<?> searchTeacherGrades(@RequestHeader("Authorization") String token, @PathVariable Long teacherID, @RequestParam String filterBy, @RequestParam String query) {
        try {
            User user = userService.readUserByUsername(jwtService.extractUsername(token.substring(7)));
            Teacher teacher = service.readTeacher(teacherID);

            if (user.getUsername().equals(teacher.getUser().getUsername())) {
                return new ResponseEntity<List<TeacherGradeView>>(gradeService.searchTeacherGrades(teacherID, filterBy, query), HttpStatus.OK);
            }

            return new ResponseEntity<String>("[UNAUTHORIZED]", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{teacherID}/grades/{gradeID}/delete") @PreAuthorize("hasAuthority('manager:delete')")
    ResponseEntity<?> removeTeacherGrade(@RequestHeader("Authorization") String token, @PathVariable Long teacherID, @PathVariable Long gradeID) {
        try {
            User user = userService.readUserByUsername(jwtService.extractUsername(token.substring(7)));
            Teacher teacher = service.readTeacher(teacherID);

            if (user.getUsername().equals(teacher.getUser().getUsername())) {
                return new ResponseEntity<String>(gradeService.deleteGrade(gradeID), HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<String>("[UNAUTHORIZED]", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{teacherID}/grades/{gradeID}/update") @PreAuthorize("hasAuthority('manager:update')")
    ResponseEntity<?> editTeacherGrade(@RequestHeader("Authorization") String token, @PathVariable Long teacherID, @PathVariable Long gradeID, @RequestParam int value) {
        try {
            User user = userService.readUserByUsername(jwtService.extractUsername(token.substring(7)));
            Teacher teacher = service.readTeacher(teacherID);

            if (user.getUsername().equals(teacher.getUser().getUsername())) {
                return new ResponseEntity<String>(gradeService.updateTeacherGrade(teacherID, gradeID, value), HttpStatus.CREATED);
            }

            return new ResponseEntity<String>("[UNAUTHORIZED]", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/feedbacks") @PreAuthorize("hasAuthority('manager:read')") @JsonView(Views.General.class)
    ResponseEntity<?> viewTeacherFeedbacks(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        try {
            User user = userService.readUserByUsername(jwtService.extractUsername(token.substring(7)));
            Teacher teacher = service.readTeacher(id);

            if (user.getUsername().equals(teacher.getUser().getUsername())) {
                return new ResponseEntity<List<TeacherFeedbackView>>(service.readTeacherFeedbacks(id), HttpStatus.OK);
            }

            return new ResponseEntity<String>("[UNAUTHORIZED]", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{teacherID}/feedbacks/{feedbackID}") @PreAuthorize("hasAuthority('manager:read')") @JsonView(Views.Private.class)
    ResponseEntity<?> viewTeacherFeedback(@RequestHeader("Authorization") String token, @PathVariable Long teacherID, @PathVariable Long feedbackID) {
        try {
            User user = userService.readUserByUsername(jwtService.extractUsername(token.substring(7)));
            Teacher teacher = service.readTeacher(teacherID);

            if (user.getUsername().equals(teacher.getUser().getUsername())) {
                return new ResponseEntity<TeacherFeedbackView>(service.readTeacherFeedback(teacherID, feedbackID), HttpStatus.OK);
            }

            return new ResponseEntity<String>("[UNAUTHORIZED]", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/{teacherID}/feedbacks/{feedbackID}/update") @PreAuthorize("hasAuthority('manager:update')")
    ResponseEntity<String> editFeedback(@RequestHeader("Authorization") String token, @PathVariable Long teacherID, @PathVariable Long feedbackID, @Valid @RequestBody FeedbackRequest request) {
        try {
            User user = userService.readUserByUsername(jwtService.extractUsername(token.substring(7)));
            Teacher teacher = service.readTeacher(teacherID);

            if (user.getUsername().equals(teacher.getUser().getUsername())) {
                return new ResponseEntity<String>(service.updateTeacherFeedback(teacherID, feedbackID, request), HttpStatus.CREATED);
            }

            return new ResponseEntity<String>("[UNAUTHORIZED]", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{teacherID}/feedbacks/{feedbackID}/delete") @PreAuthorize("hasAuthority('manager:delete')")
    ResponseEntity<String> removeFeedback(@RequestHeader("Authorization") String token, @PathVariable Long teacherID, @PathVariable Long feedbackID) {
        try {
            User user = userService.readUserByUsername(jwtService.extractUsername(token.substring(7)));
            Teacher teacher = service.readTeacher(teacherID);

            if (user.getUsername().equals(teacher.getUser().getUsername())) {
                return new ResponseEntity<String>(service.deleteFeedback(teacherID, feedbackID), HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<String>("[UNAUTHORIZED]", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}/lectures") @PreAuthorize("hasAuthority('manager:read')") @JsonView(Views.Public.class)
    ResponseEntity<?> viewTeacherLectures(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        try {
            User user = userService.readUserByUsername(jwtService.extractUsername(token.substring(7)));
            Teacher teacher = service.readTeacher(id);

            if (user.getUsername().equals(teacher.getUser().getUsername())) {
                return new ResponseEntity<List<TeacherLectureView>>(service.readTeacherLectures(id), HttpStatus.OK);
            }

            return new ResponseEntity<String>("[UNAUTHORIZED]", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{teacherID}/lectures/{lectureID}/delete") @PreAuthorize("hasAuthority('manager:delete')")
    ResponseEntity<String> deleteTeacherLecture(@RequestHeader("Authorization") String token, @PathVariable Long teacherID, @PathVariable Long lectureID) {
        try {
            User user = userService.readUserByUsername(jwtService.extractUsername(token.substring(7)));
            Teacher teacher = service.readTeacher(teacherID);

            if (user.getUsername().equals(teacher.getUser().getUsername())) {
                return new ResponseEntity<String>(lectureService.deleteTeacherLecture(teacherID, lectureID), HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<String>("[UNAUTHORIZED]", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{teacherID}/lectures/{lectureID}") @PreAuthorize("hasAuthority('manager:read')") @JsonView(Views.General.class)
    ResponseEntity<?> viewTeacherLecture(@RequestHeader("Authorization") String token, @PathVariable Long teacherID, @PathVariable Long lectureID) {
        try {
            User user = userService.readUserByUsername(jwtService.extractUsername(token.substring(7)));
            Teacher teacher = service.readTeacher(teacherID);

            if (user.getUsername().equals(teacher.getUser().getUsername())) {
                return new ResponseEntity<TeacherLectureView>(lectureService.readTeacherLecture(teacherID, lectureID), HttpStatus.OK);
            }

            return new ResponseEntity<String>("[UNAUTHORIZED]", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
