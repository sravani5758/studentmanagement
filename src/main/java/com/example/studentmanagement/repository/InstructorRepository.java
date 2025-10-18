package com.example.studentmanagement.repository;


import com.example.studentmanagement.entity.Instructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InstructorRepository extends JpaRepository<Instructor, Long> {

    Optional<Instructor> findByInstructorId(String instructorId);
    Optional<Instructor> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("SELECT c FROM Course c WHERE c.instructor.id = :instructorId")
    List<Object[]> findCoursesByInstructorId(Long instructorId);

    Optional<Instructor> findTopByOrderByIdDesc();
    @Query("SELECT i FROM Instructor i WHERE i.deleted = false")
    Page<Instructor> findAllActive(Pageable pageable);
}