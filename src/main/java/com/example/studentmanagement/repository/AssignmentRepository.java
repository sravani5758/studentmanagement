package com.example.studentmanagement.repository;


import com.example.studentmanagement.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    @Query("SELECT a FROM Assignment a WHERE a.course.id = :courseId AND a.deleted = false")
    List<Assignment> findByCourseId(Long courseId);

    @Query("SELECT a FROM Assignment a WHERE a.deleted = false")
    List<Assignment> findAllActive();

}