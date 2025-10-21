package com.example.studentmanagement.controller;

import com.example.studentmanagement.dto.request.CourseRequest;
import com.example.studentmanagement.dto.response.CourseResponse;
import com.example.studentmanagement.entity.Course;
import com.example.studentmanagement.exceptions.ResourceNotFoundException;
import com.example.studentmanagement.repository.CourseRepository;
import com.example.studentmanagement.repository.EnrollmentRepository;
import com.example.studentmanagement.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    @PostMapping
    public ResponseEntity<CourseResponse> createCourse(@RequestBody CourseRequest request) {
        return ResponseEntity.ok(courseService.createCourse(request));
    }

    @GetMapping
    public ResponseEntity<Page<CourseResponse>> getAllCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return ResponseEntity.ok(courseService.getAllCourses(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseResponse> updateCourse(
            @PathVariable Long id,
            @RequestBody CourseRequest request) {
        return ResponseEntity.ok(courseService.updateCourse(id, request));
    }

    @DeleteMapping("/{id}/soft-delete")
    public ResponseEntity<CourseResponse> softDeleteCourse(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.softDeleteCourse(id));
    }

    @GetMapping("/instructor/{instructorId}")
    public ResponseEntity<List<CourseResponse>> getCoursesByInstructor(
            @PathVariable Long instructorId) {
        return ResponseEntity.ok(courseService.getCoursesByInstructor(instructorId));
    }

    @GetMapping("/active")
    public ResponseEntity<List<CourseResponse>> getActiveCourses() {
        return ResponseEntity.ok(courseService.getActiveCourses());
    }


    @GetMapping("/search")
    public ResponseEntity<Page<CourseResponse>> searchCourses(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(courseService.searchCourseByName(name, pageable));
    }


    //  Check course seat availability
    @GetMapping("/availability/{courseId}")
    public ResponseEntity<Map<String, Object>> checkCourseAvailability(@PathVariable Long courseId) {
        try {
            Course course = courseRepository.findByIdAndNotDeleted(courseId)
                    .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

            Integer currentEnrollments = enrollmentRepository.countByCourseId(courseId);
            Integer availableSeats = course.getSeatLimit() - currentEnrollments;
            boolean isAvailable = availableSeats > 0 && !course.getDeleted() && "ACTIVE".equals(course.getStatus());

            Map<String, Object> response = new HashMap<>();
            response.put("courseId", courseId);
            response.put("courseTitle", course.getTitle());
            response.put("seatLimit", course.getSeatLimit());
            response.put("currentEnrollments", currentEnrollments);
            response.put("availableSeats", availableSeats);
            response.put("isAvailable", isAvailable);
            response.put("status", course.getStatus());
            response.put("deleted", course.getDeleted());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/availability")
    public ResponseEntity<List<Map<String, Object>>> getAllCoursesAvailability() {
        try {
            List<Course> allCourses = courseRepository.findAllActiveCourses();

            List<Map<String, Object>> availabilityList = allCourses.stream()
                    .map(course -> {
                        Long courseId = course.getId();
                        Integer currentEnrollments = enrollmentRepository.countByCourseId(courseId);
                        Integer availableSeats = course.getSeatLimit() - currentEnrollments;
                        boolean isAvailable = availableSeats > 0 && !course.getDeleted() && "ACTIVE".equals(course.getStatus());

                        Map<String, Object> response = new HashMap<>();
                        response.put("courseId", courseId);
                        response.put("courseTitle", course.getTitle());
                        response.put("seatLimit", course.getSeatLimit());
                        response.put("currentEnrollments", currentEnrollments);
                        response.put("availableSeats", availableSeats);
                        response.put("isAvailable", isAvailable);
                        response.put("status", course.getStatus());
                        response.put("deleted", course.getDeleted());

                        return response;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(availabilityList);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(List.of(Map.of("error", e.getMessage())));
        }
    }


}