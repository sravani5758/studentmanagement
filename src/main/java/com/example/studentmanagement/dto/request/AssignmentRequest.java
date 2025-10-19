package com.example.studentmanagement.dto.request;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AssignmentRequest {

    @NotBlank(message = "Title cannot be blank")
    private String title;
    private String description;
    private LocalDateTime dueDate;

    @Max(100)
    @Min(10)
    private Double maxMarks;
    private Long courseId;
}