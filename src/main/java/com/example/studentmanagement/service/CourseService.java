package com.example.studentmanagement.service;

import com.example.studentmanagement.dto.request.CourseRequest;
import com.example.studentmanagement.dto.response.CourseResponse;
import com.example.studentmanagement.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface CourseService {
    CourseResponse createCourse(CourseRequest request);
    Page<CourseResponse> getAllCourses(Pageable pageable);
    CourseResponse getCourseById(Long id);
    CourseResponse updateCourse(Long id, CourseRequest request);
    void deleteCourse(Long id);
    CourseResponse softDeleteCourse(Long id);
    List<CourseResponse> getCoursesByInstructor(Long instructorId);
    List<CourseResponse> getActiveCourses();

    Page<CourseResponse> searchCourseByName(String name, Pageable pageable);

    //List<Course> getAvailableCourses();
}