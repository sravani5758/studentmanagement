package com.example.studentmanagement.entity;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "assignments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private LocalDateTime dueDate;

    @Column(nullable = false)
    private Double maxMarks;

    @Builder.Default
    private Boolean deleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @OneToMany(mappedBy = "assignment", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Submission> submissions = new ArrayList<>();

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}