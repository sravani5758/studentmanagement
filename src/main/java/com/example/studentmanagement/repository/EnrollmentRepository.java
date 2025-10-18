package com.example.studentmanagement.repository;


import com.example.studentmanagement.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    Optional<Enrollment> findByEnrollmentNumber(String enrollmentNumber);

    List<Enrollment> findByStudentId(Long studentId);
    List<Enrollment> findByCourseId(Long courseId);

    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.id = :courseId")
    Integer countByCourseId(Long courseId);

    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);

    Optional<Enrollment> findTopByOrderByIdDesc();
}