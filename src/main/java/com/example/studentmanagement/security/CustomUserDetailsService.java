package com.example.studentmanagement.security;


import com.example.studentmanagement.entity.Admin;
import com.example.studentmanagement.entity.Instructor;
import com.example.studentmanagement.entity.Student;
import com.example.studentmanagement.repository.AdminRepository;
import com.example.studentmanagement.repository.InstructorRepository;
import com.example.studentmanagement.repository.StudentRepository;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Data
@Builder

public class CustomUserDetailsService implements UserDetailsService {

    private final StudentRepository studentRepository;
    private final InstructorRepository instructorRepository;
    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Check in Student table
        Student student = studentRepository.findByEmail(email).orElse(null);
        if (student != null) {
            return UserPrincipal.builder()
                    .id(student.getId())
                    .email(student.getEmail())
                    .password(student.getPassword())
                    .name(student.getName())
                    .role("STUDENT")
                    .build();
        }

        // Check in Instructor table
        Instructor instructor = instructorRepository.findByEmail(email).orElse(null);
        if (instructor != null) {
            return UserPrincipal.builder()
                    .id(instructor.getId())
                    .email(instructor.getEmail())
                    .password(instructor.getPassword())
                    .name(instructor.getName())
                    .role("INSTRUCTOR")
                    .build();
        }

        // Check in Admin table
        Admin admin = adminRepository.findByEmail(email).orElse(null);
        if (admin != null) {
            return UserPrincipal.builder()
                    .id(admin.getId())
                    .email(admin.getEmail())
                    .password(admin.getPassword())
                    .name(admin.getName())
                    .role("ADMIN")
                    .build();
        }

        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}