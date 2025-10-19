package com.example.studentmanagement.service.impl;

import com.example.studentmanagement.dto.request.GradeRequest;
import com.example.studentmanagement.dto.request.SubmissionRequest;
import com.example.studentmanagement.dto.response.EnrollmentResponse;
import com.example.studentmanagement.dto.response.SubmissionResponse;
import com.example.studentmanagement.entity.*;
import com.example.studentmanagement.exceptions.InvalidOperationException;
import com.example.studentmanagement.exceptions.ResourceNotFoundException;
import com.example.studentmanagement.exceptions.UnauthorizedException;
import com.example.studentmanagement.repository.*;
import com.example.studentmanagement.security.UserPrincipal;
import com.example.studentmanagement.service.SubmissionService;
import com.example.studentmanagement.util.GradeCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubmissionServiceImpl implements SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final StudentRepository studentRepository;
    private final AssignmentRepository assignmentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final GradeCalculator gradeCalculator;
    private final AdminRepository adminRepository;


    @Override
    public SubmissionResponse submitAssignment(SubmissionRequest request) {
        Student student = studentRepository.findByIdAndNotDeleted(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found or has been deleted with id: " + request.getStudentId()));

        Assignment assignment = assignmentRepository.findById(request.getAssignmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with id: " + request.getAssignmentId()));

        if (assignment.getDeleted()) {
            throw new InvalidOperationException("Assignment is not available for submission");
        }

        if (submissionRepository.findByStudentIdAndAssignmentId(student.getId(), assignment.getId()).isPresent()) {
            throw new InvalidOperationException("Assignment already submitted by this student");
        }

        String status = LocalDateTime.now().isAfter(assignment.getDueDate()) ? "LATE" : "SUBMITTED";

        Submission submission = Submission.builder()
                .submittedFile(request.getSubmittedFile())
                .student(student)
                .assignment(assignment)
                .marksObtained(null)
                .feedback(null)
                .status(status)
                .build();

        Submission savedSubmission = submissionRepository.save(submission);
        return mapToResponse(savedSubmission);
    }

    @Override
    public List<SubmissionResponse> getAllSubmissions() {
        return submissionRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SubmissionResponse getSubmissionById(Long id) {
        Submission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found with id: " + id));
        return mapToResponse(submission);
    }

    @Override
    public SubmissionResponse gradeSubmission(Long submissionId, GradeRequest request, UserPrincipal grader) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found with id: " + submissionId));

        Instructor assignedInstructor = submission.getAssignment().getCourse().getInstructor();
        if (!assignedInstructor.getId().equals(grader.getId()) && !adminRepository.existsById(grader.getId()) ) {
            throw new UnauthorizedException("You are not authorized to grade this submission");
        }

        Double maxMarks = submission.getAssignment().getMaxMarks();
        if (request.getMarksObtained()>maxMarks){
            throw new InvalidOperationException("Marks cannot exceed maximum allowed marks");
        }

        // Grade the submission
        submission.setMarksObtained(request.getMarksObtained());
        submission.setFeedback(request.getFeedback());
        submission.setStatus("GRADED");

        Submission gradedSubmission = submissionRepository.save(submission);

        //  This automatically updates enrollment grade!
        updateEnrollmentGrade(submission.getStudent().getId(), submission.getAssignment().getCourse().getId());

        // Update student GPA
        updateStudentGPA(submission.getStudent().getId());

        return mapToResponse(gradedSubmission);
    }

    @Override
    public List<SubmissionResponse> getSubmissionsByStudent(Long studentId) {
        return submissionRepository.findByStudentId(studentId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SubmissionResponse> getSubmissionBystatus(String status) {
        return submissionRepository.findByStatus(status)
                .orElseThrow(() -> new ResourceNotFoundException("No submissions found with status: " + status))
                .stream()
                .map(this::mapToResponse) // Now this works correctly
                .collect(Collectors.toList());
    }

    @Override
    public List<SubmissionResponse> getSubmissionsByAssignment(Long assignmentId) {
        return submissionRepository.findByAssignmentId(assignmentId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SubmissionResponse> getSubmissionsByCourse(Long courseId) {
        return submissionRepository.findByCourseId(courseId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private void updateEnrollmentGrade(Long studentId, Long courseId) {
        try {
            Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)
                    .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found for student " + studentId + " and course " + courseId));

            List<Submission> gradedSubmissions = submissionRepository.findByCourseId(courseId)
                    .stream()
                    .filter(s -> s.getStudent().getId().equals(studentId))
                    .filter(s -> "GRADED".equals(s.getStatus()) && s.getMarksObtained() != null)
                    .collect(Collectors.toList());

            System.out.println(" Found " + gradedSubmissions.size() + " graded submissions for enrollment update");

            if (!gradedSubmissions.isEmpty()) {
                // USE GradeCalculator: Calculate course grade with weights
                double assignmentWeight = 100.0 / gradedSubmissions.size(); // Equal weight for all assignments
                String courseGrade = gradeCalculator.calculateCourseGrade(gradedSubmissions, assignmentWeight);

                // Calculate average marks for display
                double averageMarks = gradedSubmissions.stream()
                        .mapToDouble(Submission::getMarksObtained)
                        .average()
                        .orElse(0.0);

                enrollment.setGrade(courseGrade);
                enrollment.setFinalMarks(Math.round(averageMarks * 100.0) / 100.0);
                enrollmentRepository.save(enrollment);

                System.out.println("Updated enrollment: Student " + studentId + ", Course " + courseId +
                        ", Grade: " + courseGrade + ", Marks: " + averageMarks);
            } else {
                System.out.println("No graded submissions found for enrollment update");
            }
        } catch (Exception e) {
            System.err.println("Error updating enrollment grade: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateStudentGPA(Long studentId) {
        //  USE GradeCalculator: Get enrollments with grades for proper GPA calculation
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId)
                .stream()
                .filter(e -> e.getGrade() != null)
                .collect(Collectors.toList());

        double gpa = gradeCalculator.calculateGPA(enrollments);

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        student.setGpa(gpa);
        studentRepository.save(student);

        System.out.println("Updated GPA for student " + studentId + ": " + gpa);
    }

    private SubmissionResponse mapToResponse(Submission submission) {
        SubmissionResponse response = new SubmissionResponse();
        response.setId(submission.getId());
        response.setSubmittedFile(submission.getSubmittedFile());
        response.setMarksObtained(submission.getMarksObtained());
        response.setSubmittedAt(submission.getSubmittedAt());
        response.setFeedback(submission.getFeedback());
        response.setStatus(submission.getStatus());
        response.setStudentId(submission.getStudent().getId());
        response.setStudentName(submission.getStudent().getName());
        response.setAssignmentId(submission.getAssignment().getId());
        response.setAssignmentTitle(submission.getAssignment().getTitle());
        return response;
    }
}