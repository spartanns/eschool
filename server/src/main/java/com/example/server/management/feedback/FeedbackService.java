package com.example.server.management.feedback;

import com.example.server.management.feedback.dao.FeedbackView;
import com.example.server.management.feedback.dto.FeedbackRequest;
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
    private final EmailService emailService;
    private Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

    public String createFeedback(Long teacherID, Long studentID, FeedbackRequest request) {
        Teacher teacher = teacherRepository.findById(teacherID).orElseThrow(() -> new UsernameNotFoundException("Teacher not found."));
        Student student = studentRepository.findById(studentID).orElseThrow(() -> new UsernameNotFoundException("Student not found."));

        Feedback feedback = Feedback
                .builder()
                .type(request.getType())
                .text(request.getText())
                .createdAt(new Date(System.currentTimeMillis()))
                .student(student)
                .createdBy(teacher)
                .build();
        repository.save(feedback);

        teacher.getFeedbacks().add(feedback);
        teacherRepository.save(teacher);

        student.getFeedbacks().add(feedback);
        studentRepository.save(student);

        int count = 0;

        for (Feedback f : student.getFeedbacks()) {
            if (f.getType().equals(FeedbackType.NEGATIVE)) {
                count++;
            }
        }

        if (count > 0 && count % 3 == 0 && student.getRating() > 1) {
            student.setRating(student.getRating() - 1);
            studentRepository.save(student);

            Email email = Email
                    .builder()
                    .to(student.getParent().getEmail())
                    .subject(String.format("%s %s - Rating Decreased", student.getName(), student.getSurname()))
                    .text(String.format("%s %s's school rating decreased to %d.\nReason: bad behaviour", student.getName(), student.getSurname(), student.getRating()))
                    .build();

            emailService.sendEmail(email);

            logger.info(String.format("%s %s's school rating decreased to %d.\nReason: bad behaviour", student.getName(), student.getSurname(), student.getRating()));

        }

        logger.info(String.format("%s %s gave %s %s a %s feedback.", teacher.getName(), teacher.getSurname(), student.getName(), student.getSurname(), feedback.getType().name()));

        return String.format("%s %s gave %s %s a %s feedback.", teacher.getName(), teacher.getSurname(), student.getName(), student.getSurname(), feedback.getType().name());
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


