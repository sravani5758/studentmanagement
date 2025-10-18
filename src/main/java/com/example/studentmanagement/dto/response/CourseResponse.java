package com.example.studentmanagement.dto.response;


import lombok.Data;

import java.util.List;

@Data
public class CourseResponse {
    private Long id;
    private String courseCode;
    private String title;
    private String description;
    private Integer seatLimit;     // Fixed limit only
    private String status;
    private Long instructorId;
    private String instructorName;
    private Boolean deleted;
}