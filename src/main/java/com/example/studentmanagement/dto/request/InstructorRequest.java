package com.example.studentmanagement.dto.request;


import lombok.Data;

@Data
public class InstructorRequest {
    private String name;
    private String email;
    private String password;
    private String specialization;
    private String department;
    private Long courseId;
}