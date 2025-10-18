package com.example.studentmanagement.dto.response;

import lombok.Data;

@Data
public class StudentResponse {
    private Long id;
    private String studentId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private Double gpa;
}