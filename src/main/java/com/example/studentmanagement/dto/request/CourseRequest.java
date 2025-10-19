package com.example.studentmanagement.dto.request;



import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CourseRequest {

    @NotNull(message = "Cannot be null")
    private String title;
    private String description;
    private Integer seatLimit;
    private String status;
    private Long instructorId;

}