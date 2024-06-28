package com.example.server.management.feedback.dto;

import com.example.server.management.feedback.FeedbackType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor @AllArgsConstructor @Data @Builder
public class FeedbackRequest {

    @NotNull(message = "Feedback type must be provided.")
    private FeedbackType type;

    @NotNull(message = "Feedback comment must be provided.")
    @Size(min = 4, max = 144, message = "Comment must be between {min} and {max} characters long.")
    private String text;
}
