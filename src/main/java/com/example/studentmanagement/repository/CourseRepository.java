package com.example.studentmanagement.repository;

import com.example.studentmanagement.entity.Course;
import com.example.studentmanagement.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findByCourseCode(String courseCode);

    // FIX: Get only non-deleted courses with pagination
    @Query("SELECT c FROM Course c WHERE c.deleted = false")
    Page<Course> findAllActiveCourses(Pageable pageable);

    // FIX: Get only non-deleted courses (for list)
    @Query("SELECT c FROM Course c WHERE c.deleted = false")
    List<Course> findAllActiveCourses();

    @Query("SELECT c FROM Course c WHERE c.instructor.id = :instructorId AND c.deleted = false")
    List<Course> findByInstructorId(Long instructorId);

    Optional<Course> findTopByOrderByIdDesc();

    boolean existsByTitle(String title);

    @Query("SELECT c FROM Course c WHERE c.id = :id AND c.deleted = false")
    Optional<Course> findByIdAndNotDeleted(Long id);


    @Query("SELECT c FROM Course c WHERE c.title LIKE %:name% AND c.deleted = false")
    Page<Course> findByNameContainingAndDeletedFalse(String name, Pageable pageable);

}