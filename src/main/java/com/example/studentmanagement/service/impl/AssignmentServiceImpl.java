package com.example.studentmanagement.service.impl;

import com.example.studentmanagement.dto.request.AssignmentRequest;
import com.example.studentmanagement.dto.response.AssignmentResponse;
import com.example.studentmanagement.entity.Assignment;
import com.example.studentmanagement.entity.Course;
import com.example.studentmanagement.exceptions.ResourceNotFoundException;
import com.example.studentmanagement.repository.AssignmentRepository;
import com.example.studentmanagement.repository.CourseRepository;
import com.example.studentmanagement.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssignmentServiceImpl implements AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;

    @Override
    public AssignmentResponse createAssignment(AssignmentRequest request) {
        // Find course
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new NoSuchElementException("Course not found with id: " + request.getCourseId()));

        Assignment assignment = Assignment.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .dueDate(request.getDueDate())
                .maxMarks(request.getMaxMarks())
                .course(course)
                .deleted(false)
                .build();

        Assignment savedAssignment = assignmentRepository.save(assignment);
        return mapToResponse(savedAssignment);
    }

    @Override
    public List<AssignmentResponse> getAllAssignments(Pageable pageable) {
        return assignmentRepository.findAllActive(pageable)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AssignmentResponse getAssignmentById(Long id) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Assignment not found with id: " + id));
        return mapToResponse(assignment);
    }

    @Override
    public AssignmentResponse updateAssignment(Long id, AssignmentRequest request) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Assignment not found with id: " + id));

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new NoSuchElementException("Course not found with id: " + request.getCourseId()));

        assignment.setTitle(request.getTitle());
        assignment.setDescription(request.getDescription());
        assignment.setDueDate(request.getDueDate());
        assignment.setMaxMarks(request.getMaxMarks());
        assignment.setCourse(course);

        Assignment updatedAssignment = assignmentRepository.save(assignment);
        return mapToResponse(updatedAssignment);
    }

    @Override
    public void deleteAssignment(Long id) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Assignment not found with id: " + id));
        assignmentRepository.delete(assignment);
    }

    @Override
    public void softDeleteAssignment(Long id) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with id: " + id));
        assignment.setDeleted(true);
        assignmentRepository.save(assignment);

    }

    @Override
    public List<AssignmentResponse> getAssignmentsByCourse(Long courseId) {
        return assignmentRepository.findByCourseId(courseId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AssignmentResponse> getActiveAssignments() {
        return assignmentRepository.findAllActive()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private AssignmentResponse mapToResponse(Assignment assignment) {
        AssignmentResponse response = new AssignmentResponse();
        response.setId(assignment.getId());
        response.setTitle(assignment.getTitle());
        response.setDescription(assignment.getDescription());
        response.setDueDate(assignment.getDueDate());
        response.setMaxMarks(assignment.getMaxMarks());
        response.setCourseId(assignment.getCourse().getId());
        response.setCourseTitle(assignment.getCourse().getTitle());
        response.setDeleted(assignment.getDeleted());
        response.setCreatedAt(assignment.getCreatedAt());
        return response;
    }
}