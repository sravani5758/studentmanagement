package com.example.studentmanagement.dto.request;


import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AssignmentRequest {
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private Double maxMarks;
    private Long courseId;
}