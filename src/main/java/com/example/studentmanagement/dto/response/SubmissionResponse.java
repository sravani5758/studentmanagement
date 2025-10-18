package com.example.studentmanagement.dto.response;


import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SubmissionResponse {
    private Long id;
    private String submittedFile;
    private Double marksObtained;
    private LocalDateTime submittedAt;
    private String feedback;
    private String status;
    private Long studentId;
    private String studentName;
    private Long assignmentId;
    private String assignmentTitle;
}