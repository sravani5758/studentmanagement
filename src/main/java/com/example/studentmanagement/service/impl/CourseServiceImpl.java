package com.example.studentmanagement.service.impl;

import com.example.studentmanagement.dto.request.CourseRequest;
import com.example.studentmanagement.dto.response.CourseResponse;
import com.example.studentmanagement.entity.Assignment;
import com.example.studentmanagement.entity.Course;
import com.example.studentmanagement.entity.Instructor;
import com.example.studentmanagement.exceptions.DuplicateResourceException;
import com.example.studentmanagement.repository.AssignmentRepository;
import com.example.studentmanagement.repository.CourseRepository;
import com.example.studentmanagement.repository.InstructorRepository;
import com.example.studentmanagement.service.CourseService;
import com.example.studentmanagement.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final InstructorRepository instructorRepository;
    private final IdGenerator idGenerator;

    private final AssignmentRepository assignmentRepository;
    @Override
    public CourseResponse createCourse(CourseRequest request) {
        Instructor instructor = instructorRepository.findById(request.getInstructorId())
                .orElseThrow(() -> new NoSuchElementException("Instructor not found with id: " + request.getInstructorId()));

        if (courseRepository.existsByTitle(request.getTitle())){
            throw new DuplicateResourceException("Course with this title exist");
        }

        String courseCode = idGenerator.generateCourseCode();

        Course course = Course.builder()
                .courseCode(courseCode)
                .title(request.getTitle())
                .description(request.getDescription())
                .seatLimit(request.getSeatLimit())
                .status(request.getStatus())
                .instructor(instructor)
                .deleted(false)
                .build();

        // 🔥 Update both sides of the relationship
        instructor.getCourses().add(course);

        courseRepository.save(course);

        instructorRepository.save(instructor);

        return mapToResponse(course);
    }


    @Override
    public Page<CourseResponse> getAllCourses(Pageable pageable) {
        return courseRepository.findAllActiveCourses(pageable)
                .map(this::mapToResponse);
    }

    @Override
    public CourseResponse getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Course not found with id: " + id));
        return mapToResponse(course);
    }

    @Override
    public CourseResponse updateCourse(Long id, CourseRequest request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Course not found with id: " + id));


        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setSeatLimit(request.getSeatLimit());
        course.setStatus(request.getStatus()); // Now accepts String

        Course updatedCourse = courseRepository.save(course);
        return mapToResponse(updatedCourse);
    }

    @Override
    public void deleteCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Course not found with id: " + id));
        courseRepository.delete(course);
    }

    @Override
    public CourseResponse softDeleteCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Course not found with id: " + id));
        course.setDeleted(true);
        Course updatedCourse = courseRepository.save(course);

        List<Assignment> assignments = assignmentRepository.findByCourseId(id);
        for (Assignment assignment : assignments) {
            assignment.setDeleted(true);
        }
        assignmentRepository.saveAll(assignments);
        return mapToResponse(updatedCourse);
    }

    @Override
    public List<CourseResponse> getCoursesByInstructor(Long instructorId) {
        return courseRepository.findByInstructorId(instructorId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseResponse> getActiveCourses() {
        return courseRepository.findAllActiveCourses()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private CourseResponse mapToResponse(Course course) {
        CourseResponse response = new CourseResponse();
        response.setId(course.getId());
        response.setCourseCode(course.getCourseCode());
        response.setTitle(course.getTitle());
        response.setDescription(course.getDescription());
        response.setSeatLimit(course.getSeatLimit());  // FIXED seat limit
        response.setStatus(course.getStatus());
        response.setInstructorId(course.getInstructor().getId());
        response.setInstructorName(course.getInstructor().getName());
        response.setDeleted(course.getDeleted());


        return response;
    }
}