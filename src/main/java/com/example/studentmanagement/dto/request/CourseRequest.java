package com.example.studentmanagement.dto.request;



import lombok.Data;

@Data
public class CourseRequest {
    private String title;
    private String description;
    private Integer seatLimit;
    private String status;
    private Long instructorId;

}