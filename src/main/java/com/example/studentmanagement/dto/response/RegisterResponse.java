package com.example.studentmanagement.dto.response;


import lombok.Data;

@Data
public class RegisterResponse {
    private String message;
    private Long userId;
    private String email;
    private String role;

    public RegisterResponse(String message, Long userId, String email, String role) {
        this.message = message;
        this.userId = userId;
        this.email = email;
        this.role = role;
    }
}