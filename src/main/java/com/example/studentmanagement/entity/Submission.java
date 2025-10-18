package com.example.studentmanagement.entity;



import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "submissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String submittedFile; // File path or URL

    private Double marksObtained;

    @Builder.Default
    private LocalDateTime submittedAt = LocalDateTime.now();

    private String feedback;

    @Column(nullable = false)
    private String status; // SUBMITTED, GRADED, LATE

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;
}

// Remove the enum or keep as constants
class SubmissionConstants {
    public static final String SUBMITTED = "SUBMITTED";
    public static final String GRADED = "GRADED";
    public static final String LATE = "LATE";
}