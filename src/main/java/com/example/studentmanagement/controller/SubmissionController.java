package com.example.studentmanagement.controller;

import com.example.studentmanagement.dto.request.GradeRequest;
import com.example.studentmanagement.dto.request.SubmissionRequest;
import com.example.studentmanagement.dto.response.EnrollmentResponse;
import com.example.studentmanagement.dto.response.SubmissionResponse;
import com.example.studentmanagement.security.UserPrincipal;
import com.example.studentmanagement.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    @PostMapping
    public ResponseEntity<SubmissionResponse> submitAssignment(@RequestBody SubmissionRequest request) {
        return ResponseEntity.ok(submissionService.submitAssignment(request));
    }

    @GetMapping
    public ResponseEntity<List<SubmissionResponse>> getAllSubmissions() {
        return ResponseEntity.ok(submissionService.getAllSubmissions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubmissionResponse> getSubmissionById(@PathVariable Long id) {
        return ResponseEntity.ok(submissionService.getSubmissionById(id));
    }

    @PostMapping("/{submissionId}/grade")
    public ResponseEntity<SubmissionResponse> gradeSubmission(
            @PathVariable Long submissionId,
            @RequestBody GradeRequest request,
            Authentication authentication) {

        UserPrincipal grader = (UserPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(submissionService.gradeSubmission(submissionId, request, grader));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<SubmissionResponse>> getSubmissionsByStudent(
            @PathVariable Long studentId) {
        return ResponseEntity.ok(submissionService.getSubmissionsByStudent(studentId));
    }

    @GetMapping("/assignment/{assignmentId}")
    public ResponseEntity<List<SubmissionResponse>> getSubmissionsByAssignment(
            @PathVariable Long assignmentId) {
        return ResponseEntity.ok(submissionService.getSubmissionsByAssignment(assignmentId));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<SubmissionResponse>> getSubmissionsByCourse(
            @PathVariable Long courseId) {
        return ResponseEntity.ok(submissionService.getSubmissionsByCourse(courseId));

    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<SubmissionResponse>> getSubmissionsByStatus(
            @PathVariable String status) {
        return ResponseEntity.ok(submissionService.getSubmissionBystatus(status));
    }
}