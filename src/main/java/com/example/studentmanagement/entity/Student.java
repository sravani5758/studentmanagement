package com.example.studentmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    private String studentId;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    @Email(message = "Invaild email")
    private String email;

    @Column(nullable = false)
    private String password;

    private String phone;
    private String address;

    @Builder.Default
    private Double gpa = 0.0;

    @Builder.Default
    private Boolean deleted = false; // Add soft delete field

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}