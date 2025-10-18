package com.example.studentmanagement.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "admins")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    private String adminId; // ADM-1001

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    @Email(message = "Invalid email")
    private String email;

    @Column(nullable = false)
    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    private String password;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}