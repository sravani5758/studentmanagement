package com.example.studentmanagement.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "instructors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Instructor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    private String instructorId; // INS-1001

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    @Email(message = "Invaild email")
    private String email;

    @Column(nullable = false)
    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    private String password;

    private String specialization;
    private String department;

    @Builder.Default
    private Boolean deleted = false;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "instructor", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Course> courses = new ArrayList<>();

}
