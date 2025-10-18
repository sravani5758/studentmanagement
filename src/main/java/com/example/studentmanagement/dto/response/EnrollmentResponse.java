package com.example.studentmanagement.dto.response;


import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EnrollmentResponse {
    private Long id;
    private String enrollmentNumber;
    private LocalDateTime enrolledAt;
    private String grade;
    private Double finalMarks;
    private Long studentId;
    private String studentName;
    private Long courseId;
    private String courseTitle;
}