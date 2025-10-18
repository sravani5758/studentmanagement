package com.example.studentmanagement.service;


import com.example.studentmanagement.dto.request.StudentRequest;
import com.example.studentmanagement.dto.response.StudentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudentService {
    StudentResponse createStudent(StudentRequest request);
    Page<StudentResponse> getAllStudents(Pageable pageable);
    StudentResponse getStudentById(Long id);
    StudentResponse updateStudent(Long id, StudentRequest request);
    void deleteStudent(Long id);

    Page<StudentResponse> searchStudentsByName(String name, Pageable pageable);

    void softDeleteStudent(Long id);
    StudentResponse recalculateGPA(Long studentId);
}