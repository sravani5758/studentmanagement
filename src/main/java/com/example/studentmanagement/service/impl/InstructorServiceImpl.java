package com.example.studentmanagement.service.impl;

import com.example.studentmanagement.dto.request.InstructorRequest;
import com.example.studentmanagement.dto.response.InstructorResponse;
import com.example.studentmanagement.dto.response.StudentResponse;
import com.example.studentmanagement.entity.Course;
import com.example.studentmanagement.entity.Instructor;
import com.example.studentmanagement.exceptions.DuplicateResourceException;
import com.example.studentmanagement.exceptions.ResourceNotFoundException;
import com.example.studentmanagement.repository.CourseRepository;
import com.example.studentmanagement.repository.InstructorRepository;
import com.example.studentmanagement.service.InstructorService;
import com.example.studentmanagement.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InstructorServiceImpl implements InstructorService {

    private final InstructorRepository instructorRepository;
    private final IdGenerator idGenerator;
    private final PasswordEncoder passwordEncoder;
    private final CourseRepository courseRepository;

    @Override
    public InstructorResponse createInstructor(InstructorRequest request) {
        if (instructorRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists: " + request.getEmail());
        }

        String instructorId = idGenerator.generateInstructorId();

        Instructor instructor = Instructor.builder()
                .instructorId(instructorId)
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .specialization(request.getSpecialization())
                .department(request.getDepartment())
                .deleted(false)
                .build();

        Instructor savedInstructor = instructorRepository.save(instructor);
        return mapToResponse(savedInstructor);
    }

    @Override
    public Page<InstructorResponse> searchInstructorByName(String name, Pageable pageable) {
        return instructorRepository.findByNameContainingAndDeletedFalse(name, pageable)
                .map(this::mapToResponse);
    }



    @Override
    public Page<InstructorResponse> getAllInstructors(Pageable pageable) {
        return instructorRepository.findAllActive(pageable)
                .map(this::mapToResponse);
    }

    @Override
    public InstructorResponse getInstructorById(Long id) {
        Instructor instructor = instructorRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + id));
        return mapToResponse(instructor);
    }

    @Override
    public InstructorResponse updateInstructor(Long id, InstructorRequest request) {
        Instructor instructor = instructorRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + id));

        instructor.setName(request.getName());
        instructor.setEmail(request.getEmail());
        instructor.setSpecialization(request.getSpecialization());
        instructor.setDepartment(request.getDepartment());

        Instructor updatedInstructor = instructorRepository.save(instructor);
        return mapToResponse(updatedInstructor);
    }

    @Override
    public void deleteInstructor(Long id) {
        Instructor instructor = instructorRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + id));
        instructor.setDeleted(true);
        instructorRepository.save(instructor);

        List<Course> courses = courseRepository.findByInstructorId(id);
        for (Course course : courses) {
            course.setInstructor(null);
        }
        courseRepository.saveAll(courses);
    }

    @Override
    public List<InstructorResponse> getInstructorsByDepartment(String department) {
        return instructorRepository.findAll().stream()
                .filter(instructor -> !instructor.getDeleted())
                .filter(instructor -> department.equalsIgnoreCase(instructor.getDepartment()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<InstructorResponse> getMyCourses(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return instructorRepository.findByEmail(email).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private InstructorResponse mapToResponse(Instructor instructor) {
        InstructorResponse response = new InstructorResponse();
        response.setId(instructor.getId());
        response.setInstructorId(instructor.getInstructorId());
        response.setName(instructor.getName());
        response.setEmail(instructor.getEmail());
        response.setSpecialization(instructor.getSpecialization());
        response.setDepartment(instructor.getDepartment());

        // Map courses (if any)
        if (instructor.getCourses() != null && !instructor.getCourses().isEmpty()) {
            List<InstructorResponse.CourseSummary> courseSummaries = instructor.getCourses().stream()
                    .map(course -> new InstructorResponse.CourseSummary(
                            course.getId(),
                            course.getTitle(),
                            course.getCourseCode()
                    ))
                    .collect(Collectors.toList());
            response.setCourses(courseSummaries);
        } else {
            response.setCourses(List.of()); // empty list
        }

        return response;
    }

}