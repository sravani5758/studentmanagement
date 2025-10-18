package com.example.studentmanagement.dto.request;



import lombok.Data;

@Data
public class StudentRequest {
    private String name;
    private String email;
    private String password;
    private String phone;
    private String address;
}