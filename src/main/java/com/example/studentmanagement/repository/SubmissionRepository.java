package com.example.studentmanagement.repository;


import com.example.studentmanagement.dto.response.EnrollmentResponse;
import com.example.studentmanagement.dto.response.SubmissionResponse;
import com.example.studentmanagement.entity.Submission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    List<Submission> findByStudentId(Long studentId);
    List<Submission> findByAssignmentId(Long assignmentId);

    @Query("SELECT s FROM Submission s WHERE s.assignment.course.id = :courseId")
    List<Submission> findByCourseId(Long courseId);

    Optional<Submission> findByStudentIdAndAssignmentId(Long studentId, Long assignmentId);

    Optional<List<Submission>> findByStatus(String status);
}