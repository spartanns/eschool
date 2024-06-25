package com.example.server.management;

import com.example.server.management.department.Department;
import com.example.server.management.grade.Grade;
import com.example.server.management.grade.Type;
import com.example.server.management.lecture.Lecture;
import com.example.server.management.lecture.dao.LectureView;
import com.example.server.management.subject.Subject;
import com.example.server.management.subject.dto.SubjectRequest;
import com.example.server.user.student.Student;
import com.example.server.user.teacher.Teacher;
import com.example.server.user.teacher.TeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController @RequiredArgsConstructor
@PreAuthorize("hasRole('MANAGER')") @RequestMapping("/api/v1/mgmt/teachers")
public class TeacherController {
    private final TeacherService service;

    @GetMapping("/{id}") @PreAuthorize("hasAuthority('manager:read')")
    ResponseEntity<?> singleTeacher(@PathVariable Long id) {
        try {
            return new ResponseEntity<Teacher>(service.readTeacher(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/departments") @PreAuthorize("hasAuthority('manager:read')")
    ResponseEntity<?> viewDepartments(@PathVariable Long id) {
        try {
            return new ResponseEntity<List<Department>>(service.readDepartments(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{teacherID}/departments/{deptID}") @PreAuthorize("hasAuthority('manager:read')")
    ResponseEntity<?> viewDepartment(@PathVariable Long teacherID, @PathVariable Long deptID) {
        try {
            return new ResponseEntity<Department>(service.readDepartment(teacherID, deptID), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{teacherID}/departments/{deptID}/subjects") @PreAuthorize("hasAuthority('manager:read')")
    ResponseEntity<List<Subject>> getDepartmentSubjects(@PathVariable Long teacherID, @PathVariable Long deptID) {
        return new ResponseEntity<List<Subject>>(service.readDeptSubjects(teacherID, deptID), HttpStatus.OK);
    }

    @PostMapping("/{teacherID}/departments/{deptID}/subjects/new") @PreAuthorize("hasAuthority('manager:create')")
    ResponseEntity<String> addDeptSubject(@PathVariable Long teacherID, @PathVariable Long deptID, @Valid @RequestBody SubjectRequest request) {
        try {
            return new ResponseEntity<String>(service.createSubject(teacherID, deptID, request), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{teacherID}/departments/{deptID}/subjects/{subjectID}") @PreAuthorize("hasAuthority('manager:read')")
    ResponseEntity<?> singleSubject(@PathVariable Long teacherID, @PathVariable Long deptID, @PathVariable Long subjectID) {
        try {
            return new ResponseEntity<Subject>(service.readSubject(teacherID, deptID, subjectID), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

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

    @GetMapping("/{teacherID}/departments/{deptID}/lectures") @PreAuthorize("hasAuthority('manager:read')")
    ResponseEntity<?> getAllLectures(@PathVariable Long teacherID, @PathVariable Long deptID) {
        try {
            return new ResponseEntity<List<Lecture>>(service.readLectures(teacherID, deptID), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{teacherID}/departments/{deptID}/lectures/{lectureID}") @PreAuthorize("hasAuthority('manager:read')")
    ResponseEntity<?> viewSingleLecture(@PathVariable Long teacherID, @PathVariable Long deptID, @PathVariable Long lectureID) {
        try {
            return new ResponseEntity<LectureView>(service.singleLecture(teacherID, deptID, lectureID), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{teacherID}/departments/{deptID}/lectures/{lectureID}/students") @PreAuthorize("hasAuthority('manager:read')")
    ResponseEntity<List<Student>> getAllLectureStudents(@PathVariable Long teacherID, @PathVariable Long deptID, @PathVariable Long lectureID) {
        return new ResponseEntity<List<Student>>(service.readLectureStudents(teacherID, deptID, lectureID), HttpStatus.OK);
    }

    @GetMapping("/{teacherID}/departments/{deptID}/lectures/{lectureID}/students/{studentID}") @PreAuthorize("hasAuthority('manager:read')")
    ResponseEntity<?> getLectureStudent(@PathVariable Long teacherID, @PathVariable Long deptID, @PathVariable Long lectureID, @PathVariable Long studentID) {
        try {
            return new ResponseEntity<Student>(service.readLectureStudent(teacherID, deptID, lectureID, studentID), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{teacherID}/departments/{deptID}/lectures/{lectureID}/students/{studentID}/grades") @PreAuthorize("hasAuthority('manager:read')")
    ResponseEntity<?> getLectureStudentGrades(@PathVariable Long teacherID, @PathVariable Long deptID, @PathVariable Long lectureID, @PathVariable Long studentID) {
        try {
            return new ResponseEntity<List<Grade>>(service.readLectureStudentGrades(teacherID, deptID, lectureID, studentID), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{teacherID}/departments/{deptID}/lectures/{lectureID}/students/{studentID}/grades/new") @PreAuthorize("hasAuthority('manager:update')")
    ResponseEntity<String> addLectureStudentGrade(@PathVariable Long teacherID, @PathVariable Long deptID, @PathVariable Long lectureID, @PathVariable Long studentID, @RequestParam int value, @RequestParam Type type) {
        try {
            return new ResponseEntity<String>(service.updateLectureStudentGrades(teacherID, deptID, lectureID, studentID, value, type), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
