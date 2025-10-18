package com.example.studentmanagement.service;

import com.example.studentmanagement.dto.request.AssignmentRequest;
import com.example.studentmanagement.dto.response.AssignmentResponse;

import java.util.List;

public interface AssignmentService {
    AssignmentResponse createAssignment(AssignmentRequest request);
    List<AssignmentResponse> getAllAssignments();
    AssignmentResponse getAssignmentById(Long id);
    AssignmentResponse updateAssignment(Long id, AssignmentRequest request);
    void deleteAssignment(Long id);
    void softDeleteAssignment(Long id);
    List<AssignmentResponse> getAssignmentsByCourse(Long courseId);
    List<AssignmentResponse> getActiveAssignments();
}
