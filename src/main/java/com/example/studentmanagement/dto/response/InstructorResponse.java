package com.example.studentmanagement.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class InstructorResponse {
    private Long id;
    private String instructorId;
    private String name;
    private String email;
    private String specialization;
    private String department;

    private List<CourseSummary> courses;
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CourseSummary {
        private Long courseId;
        private String courseTitle;
        private String courseCode;
    }
}