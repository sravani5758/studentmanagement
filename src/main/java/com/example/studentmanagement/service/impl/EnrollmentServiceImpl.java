package com.example.studentmanagement.service.impl;

import com.example.studentmanagement.dto.request.EnrollmentRequest;
import com.example.studentmanagement.dto.response.EnrollmentResponse;
import com.example.studentmanagement.entity.*;
import com.example.studentmanagement.exceptions.DuplicateResourceException;
import com.example.studentmanagement.exceptions.InvalidOperationException;
import com.example.studentmanagement.exceptions.ResourceNotFoundException;
import com.example.studentmanagement.repository.EnrollmentRepository;
import com.example.studentmanagement.repository.StudentRepository;
import com.example.studentmanagement.repository.CourseRepository;
import com.example.studentmanagement.repository.SubmissionRepository;
import com.example.studentmanagement.service.EnrollmentService;
import com.example.studentmanagement.util.GradeCalculator;
import com.example.studentmanagement.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final SubmissionRepository submissionRepository;
    private final GradeCalculator gradeCalculator;
    private final IdGenerator idGenerator;

    @Override
    public EnrollmentResponse enrollStudent(EnrollmentRequest request) {
        Student student = studentRepository.findByIdAndNotDeleted(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + request.getStudentId()));

        Course course = courseRepository.findByIdAndNotDeleted(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + request.getCourseId()));

        if (!"ACTIVE".equals(course.getStatus())) {
            throw new InvalidOperationException("Course is not available for enrollment");
        }

        if (enrollmentRepository.findByStudentIdAndCourseId(student.getId(), course.getId()).isPresent()) {
            throw new DuplicateResourceException("Student is already enrolled in this course");
        }

        Integer currentEnrollments = enrollmentRepository.countByCourseId(course.getId());
        System.out.println(" Course: " + course.getTitle() + ", Seat Limit: " + course.getSeatLimit() + ", Current Enrollments: " + currentEnrollments);

        if (currentEnrollments >= course.getSeatLimit()) {
            throw new InvalidOperationException("Course is full. No seats available. Current: " + currentEnrollments + "/" + course.getSeatLimit());
        }

        String enrollmentNumber = idGenerator.generateEnrollmentNumber();

        Enrollment enrollment = Enrollment.builder()
                .enrollmentNumber(enrollmentNumber)
                .student(student)
                .course(course)
                .grade(null)
                .finalMarks(null)
                .build();

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);

        Integer updatedCount = enrollmentRepository.countByCourseId(course.getId());
        System.out.println("Enrollment successful! Updated count: " + updatedCount + "/" + course.getSeatLimit());

        return mapToResponse(savedEnrollment);
    }

    @Override
    public Page<EnrollmentResponse> getAllEnrollments(Pageable pageable) {
        return enrollmentRepository.findAllByNotDeleted(pageable)
                .map(this::mapToResponse);
    }

    @Override
    public EnrollmentResponse getEnrollmentById(Long id) {
        Enrollment enrollment = enrollmentRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + id));
        return mapToResponse(enrollment);
    }

    @Override
    public void deleteEnrollment(Long id) {
        Enrollment enrollment = enrollmentRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + id));
        enrollment.setDeleted(true);
        enrollmentRepository.save(enrollment);
    }

    @Override
    public List<EnrollmentResponse> getEnrollmentsByStudent(Long studentId) {
        return enrollmentRepository.findByStudentIdAndNotDeleted(studentId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<EnrollmentResponse> getEnrollmentsByCourse(Long courseId) {
        return enrollmentRepository.findByCourseIdAndNotDeleted(courseId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public EnrollmentResponse updateGrade(Long enrollmentId, String grade, Double marks) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + enrollmentId));

        //  Validate grade format
        String calculatedGrade = gradeCalculator.convertMarksToGrade(marks);

        enrollment.setGrade(calculatedGrade);
        enrollment.setFinalMarks(marks);

        Enrollment updatedEnrollment = enrollmentRepository.save(enrollment);

        // Update student GPA
        updateStudentGPA(enrollment.getStudent().getId());

        return mapToResponse(updatedEnrollment);



    }
    @Override
    public EnrollmentResponse getByStudentAndCourseId(Long studentId, Long courseId){
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseIdAndDeletedFalse(studentId,courseId)
                .orElseThrow(()-> new ResourceNotFoundException("No enrollment found"));
        return mapToResponse(enrollment);
    }

    //  Calculate final grade using GradeCalculator
    public EnrollmentResponse calculateFinalGrade(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findByIdAndNotDeleted(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + enrollmentId));

        //  Only get GRADED submissions
        List<Submission> gradedSubmissions = submissionRepository.findByCourseId(enrollment.getCourse().getId())
                .stream()
                .filter(s -> s.getStudent().getId().equals(enrollment.getStudent().getId()))
                .filter(s -> "GRADED".equals(s.getStatus()) && s.getMarksObtained() != null) // ← ONLY GRADED!
                .collect(Collectors.toList());

        System.out.println("calculateFinalGrade - Found " + gradedSubmissions.size() + " graded submissions");

        if (gradedSubmissions.isEmpty()) {
            throw new ResourceNotFoundException("No graded submissions found to calculate final grade");
        }

        //  FIX: Calculate with proper weights for GRADED submissions only
        double assignmentWeight = 100.0 / gradedSubmissions.size();
        String finalGrade = gradeCalculator.calculateCourseGrade(gradedSubmissions, assignmentWeight);

        // Calculate average marks from GRADED submissions only
        double averageMarks = gradedSubmissions.stream()
                .mapToDouble(Submission::getMarksObtained)
                .average()
                .orElse(0.0);

        enrollment.setGrade(finalGrade);
        enrollment.setFinalMarks(Math.round(averageMarks * 100.0) / 100.0);

        Enrollment updatedEnrollment = enrollmentRepository.save(enrollment);

        System.out.println(" Calculated final grade: " + finalGrade + " with marks: " + averageMarks);

        return mapToResponse(updatedEnrollment);
    }

    private void updateStudentGPA(Long studentId) {
        //  USE GradeCalculator: Proper GPA calculation from course grades
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId)
                .stream()
                .filter(e -> e.getGrade() != null)
                .collect(Collectors.toList());

        double gpa = gradeCalculator.calculateGPA(enrollments);

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        student.setGpa(gpa);
        studentRepository.save(student);

        System.out.println(" Updated GPA for student " + studentId + ": " + gpa);
    }

    private EnrollmentResponse mapToResponse(Enrollment enrollment) {
        EnrollmentResponse response = new EnrollmentResponse();
        response.setId(enrollment.getId());
        response.setEnrollmentNumber(enrollment.getEnrollmentNumber());
        response.setEnrolledAt(enrollment.getEnrolledAt());
        response.setGrade(enrollment.getGrade());
        response.setFinalMarks(enrollment.getFinalMarks());
        response.setStudentId(enrollment.getStudent().getId());
        response.setStudentName(enrollment.getStudent().getName());
        response.setCourseId(enrollment.getCourse().getId());
        response.setCourseTitle(enrollment.getCourse().getTitle());
        return response;
    }
}