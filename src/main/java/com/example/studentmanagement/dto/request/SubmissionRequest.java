package com.example.studentmanagement.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmissionRequest {
    private Long studentId;
    private Long assignmentId;
    @NotNull(message = "Cannot be null")
    private String submittedFile; // File path or URL
}