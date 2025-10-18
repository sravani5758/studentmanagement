package com.example.studentmanagement.controller;

import com.example.studentmanagement.dto.request.EnrollmentRequest;
import com.example.studentmanagement.dto.response.EnrollmentResponse;
import com.example.studentmanagement.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    public ResponseEntity<EnrollmentResponse> enrollStudent(@RequestBody EnrollmentRequest request) {
        return ResponseEntity.ok(enrollmentService.enrollStudent(request));
    }

    @GetMapping
    public ResponseEntity<Page<EnrollmentResponse>> getAllEnrollments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "enrolledAt") String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        return ResponseEntity.ok(enrollmentService.getAllEnrollments(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnrollmentResponse> getEnrollmentById(@PathVariable Long id) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentById(id));
    }

    @DeleteMapping("/{id}/soft-delete")
    public ResponseEntity<Void> UnEnrollment(@PathVariable Long id) {
        enrollmentService.deleteEnrollment(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<EnrollmentResponse>> getEnrollmentsByStudent(
            @PathVariable Long studentId) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByStudent(studentId));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<EnrollmentResponse>> getEnrollmentsByCourse(
            @PathVariable Long courseId) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByCourse(courseId));
    }

    @PatchMapping("/{enrollmentId}/grade")
    public ResponseEntity<EnrollmentResponse> updateGrade(
            @PathVariable Long enrollmentId,
            @RequestParam String grade,
            @RequestParam Double marks) {
        return ResponseEntity.ok(enrollmentService.updateGrade(enrollmentId, grade, marks));
    }

    // ✅ NEW ENDPOINT: Calculate final grade using GradeCalculator
    @PostMapping("/{enrollmentId}/calculate-final-grade")
    public ResponseEntity<EnrollmentResponse> calculateFinalGrade(@PathVariable Long enrollmentId) {
        return ResponseEntity.ok(enrollmentService.calculateFinalGrade(enrollmentId));
    }
}