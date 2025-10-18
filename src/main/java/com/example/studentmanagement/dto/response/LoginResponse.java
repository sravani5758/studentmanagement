package com.example.studentmanagement.dto.response;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String email;
    private String name;
    private String role;

    public LoginResponse(String token, Long id, String email, String name, String role) {
        this.token = token;
        this.id = id;
        this.email = email;
        this.name = name;
        this.role = role;
    }
}