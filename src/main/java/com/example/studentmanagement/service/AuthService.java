package com.example.studentmanagement.service;


import com.example.studentmanagement.dto.request.LoginRequest;
import com.example.studentmanagement.dto.request.RegisterRequest;
import com.example.studentmanagement.dto.response.LoginResponse;
import com.example.studentmanagement.dto.response.RegisterResponse;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
}