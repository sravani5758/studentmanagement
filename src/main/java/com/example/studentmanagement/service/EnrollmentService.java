package com.example.studentmanagement.service;

import com.example.studentmanagement.dto.request.EnrollmentRequest;
import com.example.studentmanagement.dto.response.EnrollmentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface EnrollmentService {
    EnrollmentResponse enrollStudent(EnrollmentRequest request);
    Page<EnrollmentResponse> getAllEnrollments(Pageable pageable);
    EnrollmentResponse getEnrollmentById(Long id);
    void deleteEnrollment(Long id);
    List<EnrollmentResponse> getEnrollmentsByStudent(Long studentId);
    List<EnrollmentResponse> getEnrollmentsByCourse(Long courseId);
    EnrollmentResponse updateGrade(Long enrollmentId, String grade, Double marks);
    EnrollmentResponse calculateFinalGrade(Long enrollmentId);

    EnrollmentResponse getByStudentAndCourseId(Long studentId, Long courseId);

}