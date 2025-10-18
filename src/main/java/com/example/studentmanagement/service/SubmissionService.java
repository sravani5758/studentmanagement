package com.example.studentmanagement.service;

import com.example.studentmanagement.dto.request.GradeRequest;
import com.example.studentmanagement.dto.request.SubmissionRequest;
import com.example.studentmanagement.dto.response.SubmissionResponse;
import com.example.studentmanagement.security.UserPrincipal;

import java.util.List;

public interface SubmissionService {
    SubmissionResponse submitAssignment(SubmissionRequest request);
    List<SubmissionResponse> getAllSubmissions();
    SubmissionResponse getSubmissionById(Long id);
    SubmissionResponse gradeSubmission(Long submissionId, GradeRequest request, UserPrincipal grader); // UserPrincipal here
    List<SubmissionResponse> getSubmissionsByStudent(Long studentId);
    List<SubmissionResponse> getSubmissionsByAssignment(Long assignmentId);
    List<SubmissionResponse> getSubmissionsByCourse(Long courseId);
}