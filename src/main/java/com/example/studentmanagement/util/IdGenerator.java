package com.example.studentmanagement.util;


import com.example.studentmanagement.entity.Course;
import com.example.studentmanagement.entity.Enrollment;
import com.example.studentmanagement.entity.Instructor;
import com.example.studentmanagement.entity.Student;
import com.example.studentmanagement.repository.StudentRepository;
import com.example.studentmanagement.repository.InstructorRepository;
import com.example.studentmanagement.repository.CourseRepository;
import com.example.studentmanagement.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class IdGenerator {

    private final StudentRepository studentRepository;
    private final InstructorRepository instructorRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    public String generateStudentId() {
        // FIX: Use findAll and get the first result instead of findTop
        List<Student> students = studentRepository.findAll();
        if (students.isEmpty()) {
            return "STU-1001";
        }

        // Get the last student by ID
        Student lastStudent = students.get(students.size() - 1);
        String lastStudentId = lastStudent.getStudentId();

        if (lastStudentId != null && lastStudentId.startsWith("STU-")) {
            Long lastNumber = Long.parseLong(lastStudentId.substring(4));
            return "STU-" + (lastNumber + 1);
        }
        return "STU-1001";
    }

    public String generateInstructorId() {
        // FIX: Use findAll and get the first result instead of findTop
        List<Instructor> instructors = instructorRepository.findAll();
        if (instructors.isEmpty()) {
            return "INS-1001";
        }

        // Get the last instructor by ID
        Instructor lastInstructor = instructors.get(instructors.size() - 1);
        String lastInstructorId = lastInstructor.getInstructorId();

        if (lastInstructorId != null && lastInstructorId.startsWith("INS-")) {
            Long lastNumber = Long.parseLong(lastInstructorId.substring(4));
            return "INS-" + (lastNumber + 1);
        }
        return "INS-1001";
    }

    public String generateCourseCode() {
        // FIX: Use findAll and get the first result instead of findTop
        List<Course> courses = courseRepository.findAll();
        if (courses.isEmpty()) {
            return "CRS-1001";
        }

        // Get the last course by ID
        Course lastCourse = courses.get(courses.size() - 1);
        String lastCourseCode = lastCourse.getCourseCode();

        if (lastCourseCode != null && lastCourseCode.startsWith("CRS-")) {
            Long lastNumber = Long.parseLong(lastCourseCode.substring(4));
            return "CRS-" + (lastNumber + 1);
        }
        return "CRS-1001";
    }

    public String generateEnrollmentNumber() {
        String datePrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // FIX: Use findAll and get the first result instead of findTop
        List<Enrollment> enrollments = enrollmentRepository.findAll();
        if (enrollments.isEmpty()) {
            return "ENR-" + datePrefix + "-0001";
        }

        // Get the last enrollment by ID
        Enrollment lastEnrollment = enrollments.get(enrollments.size() - 1);
        String lastEnrollmentNumber = lastEnrollment.getEnrollmentNumber();

        if (lastEnrollmentNumber != null && lastEnrollmentNumber.startsWith("ENR-" + datePrefix + "-")) {
            Long lastNumber = Long.parseLong(lastEnrollmentNumber.substring(13));
            return "ENR-" + datePrefix + "-" + String.format("%04d", lastNumber + 1);
        }
        return "ENR-" + datePrefix + "-0001";
    }
}