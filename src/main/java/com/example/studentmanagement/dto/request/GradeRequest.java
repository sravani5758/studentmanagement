package com.example.studentmanagement.dto.request;


import lombok.Data;

@Data
public class GradeRequest {
    private Double marksObtained;
    private String feedback;
}