package com.example.server.management.feedback;

import com.example.server.admin.department.Department;
import com.example.server.management.feedback.dao.FeedbackView;
import com.example.server.management.feedback.dto.FeedbackRequest;
import com.example.server.management.lecture.Lecture;
import com.example.server.management.lecture.LectureRepository;
import com.example.server.management.subject.Subject;
import com.example.server.user.student.Student;
import com.example.server.user.student.StudentRepository;
import com.example.server.user.teacher.Teacher;
import com.example.server.user.teacher.TeacherRepository;
import com.example.server.user.teacher.dao.GradeTeacherView;
import com.example.server.util.email.Email;
import com.example.server.util.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service @RequiredArgsConstructor
public class FeedbackService {
    private final FeedbackRepository repository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final LectureRepository lectureRepository;
    private final EmailService emailService;
    private Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

    public String createFeedback(Long teacherID, Long deptID, Long subjectID, Long lectureID, Long studentID, FeedbackRequest request) {
        Teacher teacher = teacherRepository.findById(teacherID).orElseThrow(() -> new UsernameNotFoundException("Teacher not found."));

        for (Department d : teacher.getDepartments()) {
            if (d.getId().equals(deptID)) {
                for (Subject s : d.getSubjects()) {
                    if (s.getId().equals(subjectID)) {
                        for (Lecture l : s.getLectures()) {
                            if (l.getId().equals(lectureID)) {
                                for (Student student : l.getAttendants()) {
                                    if (student.getId().equals(studentID)) {
                                        Feedback feedback = Feedback
                                                .builder()
                                                .type(request.getType())
                                                .text(request.getText())
                                                .createdBy(teacher)
                                                .createdAt(new Date(System.currentTimeMillis()))
                                                .student(student)
                                                .lecture(l)
                                                .build();
                                        repository.save(feedback);
                                        student.getFeedbacks().add(feedback);

                                        int count = 0;

                                        for (Feedback f : student.getFeedbacks()) {
                                            if (f.getType().equals(FeedbackType.NEGATIVE)) {
                                                count++;
                                            }
                                        }

                                        if (count > 0 && count % 3 == 0) {
                                            student.setRating(student.getRating() - 1);

                                            Email email = Email
                                                    .builder()
                                                    .to(student.getParent().getEmail())
                                                    .subject(String.format("%s %s - Rating Decreased", student.getName(), student.getSurname()))
                                                    .text(String.format("%s %s's school rating decreased to %d.\nReason: bad behaviour", student.getName(), student.getSurname(), student.getRating()))
                                                    .build();
                                            emailService.sendEmail(email);
                                        }

                                        studentRepository.save(student);
                                        l.getFeedbacks().add(feedback);
                                        lectureRepository.save(l);
                                        teacher.getFeedbacks().add(feedback);
                                        teacherRepository.save(teacher);

                                        logger.info(String.format("%s %s gave %s %s a %s feedback.", teacher.getName(), teacher.getSurname(), student.getName(), student.getSurname(), feedback.getType().name()));

                                        return String.format("%s %s gave %s %s a %s feedback.", teacher.getName(), teacher.getSurname(), student.getName(), student.getSurname(), feedback.getType().name());
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

    public List<Feedback> index() {
        return repository.findAll();
    }

    public String adminCreateFeedback(FeedbackRequest request) {
        Feedback feedback = Feedback
                .builder()
                .createdAt(new Date(System.currentTimeMillis()))
                .text(request.getText())
                .type(request.getType())
                .build();
        repository.save(feedback);

        logger.info(String.format("Feedback with ID: %d created.", feedback.getId()));

        return String.format("Feedback with ID: %d created.", feedback.getId());
    }

    public String deleteFeedback(Long id) {
        Feedback feedback = repository.findById(id).orElseThrow(() -> new UsernameNotFoundException("Feedback not found."));
        repository.delete(feedback);

        logger.warn(String.format("Feedback with ID: %d deleted.", id));

        return String.format("Feedback with ID: %d deleted.", id);
    }

    public List<FeedbackView> readStudentFeedbacks(Long id) {
        List<FeedbackView> feedbacks = new ArrayList<>();
        Student student = studentRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("Student not found."));

        for (Feedback f : student.getFeedbacks()) {
            GradeTeacherView teacher = GradeTeacherView
                    .builder()
                    .teacherID(f.getCreatedBy().getId())
                    .name(f.getCreatedBy().getName())
                    .surname(f.getCreatedBy().getSurname())
                    .build();

            FeedbackView feedback = FeedbackView
                    .builder()
                    .id(f.getId())
                    .type(f.getType())
                    .text(f.getText())
                    .createdBy(teacher)
                    .createdAt(f.getCreatedAt())
                    .build();
            feedbacks.add(feedback);
        }

        return feedbacks;
    }

    public FeedbackView readStudentFeedback(Long studentID, Long feedbackID) {
        Student student = studentRepository.findById(studentID).orElseThrow(() -> new UsernameNotFoundException("Student not found."));

        for (Feedback f : student.getFeedbacks()) {
            if (f.getId().equals(feedbackID)) {
                GradeTeacherView teacher = GradeTeacherView
                        .builder()
                        .teacherID(f.getCreatedBy().getId())
                        .name(f.getCreatedBy().getName())
                        .surname(f.getCreatedBy().getSurname())
                        .build();

                FeedbackView feedback = FeedbackView
                        .builder()
                        .id(f.getId())
                        .type(f.getType())
                        .text(f.getText())
                        .createdAt(f.getCreatedAt())
                        .createdBy(teacher)
                        .build();

                return feedback;
            }
        }
        throw new UsernameNotFoundException("Feedback not found.");
    }
}


