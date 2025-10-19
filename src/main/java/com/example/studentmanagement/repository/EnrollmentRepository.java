package com.example.studentmanagement.repository;


import com.example.studentmanagement.dto.response.EnrollmentResponse;
import com.example.studentmanagement.entity.Enrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    Optional<Enrollment> findByEnrollmentNumber(String enrollmentNumber);

    List<Enrollment> findByStudentId(Long studentId);
    List<Enrollment> findByCourseId(Long courseId);

    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.id = :courseId AND e.deleted=false")
    Integer countByCourseId(Long courseId);

    @Query("SELECT e FROM Enrollment e WHERE e.deleted = false")
    Page<Enrollment> findAllByNotDeleted(Pageable pageable);

    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);

    @Query("SELECT e FROM Enrollment e WHERE e.id = :id AND e.deleted = false")
    Optional<Enrollment> findByIdAndNotDeleted(Long id);

    Optional<Enrollment> findTopByOrderByIdDesc();

    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId AND e.deleted = false")
    Optional<Enrollment> findByStudentIdAndNotDeleted(Long studentId);

    @Query("SELECT e FROM Enrollment e WHERE e.course.id = :courseId AND e.deleted = false")
    Optional<Enrollment> findByCourseIdAndNotDeleted(Long courseId);

    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId AND e.course.id = :courseId AND e.deleted = false")
    Optional<Enrollment> findByStudentIdAndCourseIdAndDeletedFalse(Long studentId, Long courseId);

}