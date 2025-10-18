package com.example.studentmanagement.controller;


import com.example.studentmanagement.dto.request.LoginRequest;
import com.example.studentmanagement.dto.request.RegisterRequest;
import com.example.studentmanagement.dto.response.LoginResponse;
import com.example.studentmanagement.dto.response.RegisterResponse;
import com.example.studentmanagement.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}