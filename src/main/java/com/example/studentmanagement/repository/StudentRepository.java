package com.example.studentmanagement.repository;

import com.example.studentmanagement.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    // FIX: Only find non-deleted students
    @Query("SELECT s FROM Student s WHERE s.studentId = :studentId AND s.deleted = false")
    Optional<Student> findByStudentId(String studentId);

    @Query("SELECT s FROM Student s WHERE s.email = :email AND s.deleted = false")
    Optional<Student> findByEmail(String email);

    // FIX: Check if email exists in non-deleted students only
    @Query("SELECT COUNT(s) > 0 FROM Student s WHERE s.email = :email AND s.deleted = false")
    boolean existsByEmail(String email);

    // FIX: Get only non-deleted students
    @Query("SELECT s FROM Student s WHERE s.deleted = false")
    Page<Student> findAllActive(Pageable pageable);

    // FIX: Search only non-deleted students
    @Query("SELECT s FROM Student s WHERE s.name LIKE %:name% AND s.deleted = false")
    Page<Student> findByNameContainingAndDeletedFalse(String name, Pageable pageable);

    // FIX: Get latest non-deleted student for ID generation
    @Query("SELECT s FROM Student s WHERE s.deleted = false ORDER BY s.id DESC")
    Optional<Student> findTopByOrderByIdDesc();

    // FIX: Important! Override the default findById to check deletion status
    @Query("SELECT s FROM Student s WHERE s.id = :id AND s.deleted = false")
    Optional<Student> findByIdAndNotDeleted(Long id);

    @Query("select s from Student s where s.deleted = true")
    Optional<Student> findDeletedStudents();
}