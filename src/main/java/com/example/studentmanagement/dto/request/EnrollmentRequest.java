package com.example.studentmanagement.dto.request;


import lombok.Data;

@Data
public class EnrollmentRequest {
    private Long studentId;
    private Long courseId;
}