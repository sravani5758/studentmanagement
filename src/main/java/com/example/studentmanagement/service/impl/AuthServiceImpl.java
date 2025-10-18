package com.example.studentmanagement.service.impl;


import com.example.studentmanagement.dto.request.LoginRequest;
import com.example.studentmanagement.dto.request.RegisterRequest;
import com.example.studentmanagement.dto.response.LoginResponse;
import com.example.studentmanagement.dto.response.RegisterResponse;
import com.example.studentmanagement.entity.Admin;
import com.example.studentmanagement.entity.Instructor;
import com.example.studentmanagement.entity.Student;
import com.example.studentmanagement.repository.AdminRepository;
import com.example.studentmanagement.repository.InstructorRepository;
import com.example.studentmanagement.repository.StudentRepository;
import com.example.studentmanagement.security.JwtUtil;
import com.example.studentmanagement.security.UserPrincipal;
import com.example.studentmanagement.service.AuthService;
import com.example.studentmanagement.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final StudentRepository studentRepository;
    private final InstructorRepository instructorRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final IdGenerator idGenerator;

    @Override
    public RegisterResponse register(RegisterRequest request) {
        // Check if email exists in any table
        if (studentRepository.existsByEmail(request.getEmail()) ||
                instructorRepository.existsByEmail(request.getEmail()) ||
                adminRepository.existsByEmail(request.getEmail())) {
            return new RegisterResponse("Error: Email is already taken!", null, null, null);
        }

        switch (request.getRole().toUpperCase()) {
            case "STUDENT":
                return registerStudent(request);
            case "INSTRUCTOR":
                return registerInstructor(request);
            case "ADMIN":
                return registerAdmin(request);
            default:
                return new RegisterResponse("Error: Invalid role!", null, null, null);
        }
    }

    private RegisterResponse registerStudent(RegisterRequest request) {
        String studentId = idGenerator.generateStudentId();

        Student student = Student.builder()
                .studentId(studentId)
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        Student savedStudent = studentRepository.save(student);
        return new RegisterResponse("Student registered successfully!",
                savedStudent.getId(), savedStudent.getEmail(), "STUDENT");
    }

    private RegisterResponse registerInstructor(RegisterRequest request) {
        String instructorId = idGenerator.generateInstructorId();

        Instructor instructor = Instructor.builder()
                .instructorId(instructorId)
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        Instructor savedInstructor = instructorRepository.save(instructor);
        return new RegisterResponse("Instructor registered successfully!",
                savedInstructor.getId(), savedInstructor.getEmail(), "INSTRUCTOR");
    }

    private RegisterResponse registerAdmin(RegisterRequest request) {
        // Generate admin ID
        String adminId = "ADM-" + (adminRepository.count() + 1001);

        Admin admin = Admin.builder()
                .adminId(adminId)
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        Admin savedAdmin = adminRepository.save(admin);
        return new RegisterResponse("Admin registered successfully!",
                savedAdmin.getId(), savedAdmin.getEmail(), "ADMIN");
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtil.generateToken(authentication);

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        return new LoginResponse(jwt, userPrincipal.getId(), userPrincipal.getEmail(),
                userPrincipal.getName(), userPrincipal.getRole());
    }
}