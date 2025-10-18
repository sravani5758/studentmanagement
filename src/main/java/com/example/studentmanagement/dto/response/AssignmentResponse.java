package com.example.studentmanagement.dto.response;



import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AssignmentResponse {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private Double maxMarks;
    private Long courseId;
    private String courseTitle;
    private Boolean deleted;
    private LocalDateTime createdAt;
}