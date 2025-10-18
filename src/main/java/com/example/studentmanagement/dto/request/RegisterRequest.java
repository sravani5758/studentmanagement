package com.example.studentmanagement.dto.request;


import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private String role; // STUDENT, INSTRUCTOR, ADMIN
}