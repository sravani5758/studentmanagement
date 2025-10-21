package com.example.studentmanagement.service;

import com.example.studentmanagement.dto.request.InstructorRequest;
import com.example.studentmanagement.dto.response.InstructorResponse;
import com.example.studentmanagement.dto.response.StudentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface InstructorService {
    InstructorResponse createInstructor(InstructorRequest request);
    Page<InstructorResponse> getAllInstructors(Pageable pageable);
    InstructorResponse getInstructorById(Long id);
    InstructorResponse updateInstructor(Long id, InstructorRequest request);
    void deleteInstructor(Long id);
    List<InstructorResponse> getInstructorsByDepartment(String department);

    Page<InstructorResponse> searchInstructorByName(String name, Pageable pageable);

    List<InstructorResponse> getMyCourses();
}