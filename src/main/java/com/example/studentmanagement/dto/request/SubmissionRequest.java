package com.example.studentmanagement.dto.request;


import lombok.Data;

@Data
public class SubmissionRequest {
    private Long studentId;
    private Long assignmentId;
    private String submittedFile; // File path or URL
}