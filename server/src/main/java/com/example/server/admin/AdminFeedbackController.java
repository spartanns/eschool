package com.example.server.admin;

import com.example.server.management.feedback.Feedback;
import com.example.server.management.feedback.FeedbackService;
import com.example.server.management.feedback.dto.FeedbackRequest;
import com.example.server.security.Views;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController @RequiredArgsConstructor
@RequestMapping("/api/v1/admin/feedbacks") @PreAuthorize("hasRole('ADMIN')")
public class AdminFeedbackController {
    private final FeedbackService service;

    @GetMapping @PreAuthorize("hasAuthority('admin:read')") @JsonView(Views.Public.class)
    ResponseEntity<?> getAllFeedbacks() {
        try {
            return new ResponseEntity<List<Feedback>>(service.index(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}") @PreAuthorize("hasAuthority('admin:read')") @JsonView(Views.Public.class)
    ResponseEntity<?> viewFeedback(@PathVariable Long id) {
        try {
            return new ResponseEntity<Feedback>(service.readFeedback(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/new") @PreAuthorize("hasAuthority('admin:update')")
    ResponseEntity<String> addNewFeedback(@Valid @RequestBody FeedbackRequest request) {
       try {
        return new ResponseEntity<String>(service.adminCreateFeedback(request), HttpStatus.CREATED);
       } catch (Exception e) {
           return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
       }
    }

    @DeleteMapping("/{id}/delete") @PreAuthorize("hasAuthority('admin:delete')")
    ResponseEntity<String> deleteFeedback(@PathVariable Long id) {
        try {
            return new ResponseEntity<String>(service.deleteFeedback(id), HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/{id}/update") @PreAuthorize("hasAuthority('admin:update')")
    ResponseEntity<String> updateFeedback(@PathVariable Long id, @Valid @RequestBody FeedbackRequest request) {
        try {
            return new ResponseEntity<String>(service.updateFeedback(id, request), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
