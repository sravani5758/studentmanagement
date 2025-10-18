package com.example.studentmanagement.service.impl;

import com.example.studentmanagement.dto.request.StudentRequest;
import com.example.studentmanagement.dto.response.StudentResponse;
import com.example.studentmanagement.entity.Student;
import com.example.studentmanagement.repository.StudentRepository;
import com.example.studentmanagement.repository.EnrollmentRepository;
import com.example.studentmanagement.service.StudentService;
import com.example.studentmanagement.util.GradeCalculator;
import com.example.studentmanagement.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final IdGenerator idGenerator;
    private final PasswordEncoder passwordEncoder;
    private final GradeCalculator gradeCalculator;

    @Override
    public StudentResponse createStudent(StudentRequest request) {
        if (studentRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }

        String studentId = idGenerator.generateStudentId();

        Student student = Student.builder()
                .studentId(studentId)
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .address(request.getAddress())
                .gpa(0.0)
                .deleted(false)
                .build();

        Student savedStudent = studentRepository.save(student);
        return mapToResponse(savedStudent);
    }

    @Override
    public Page<StudentResponse> getAllStudents(Pageable pageable) {
        return studentRepository.findAllActive(pageable)
                .map(this::mapToResponse);
    }

    @Override
    public StudentResponse getStudentById(Long id) {
        Student student = studentRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new NoSuchElementException("Student not found with id: " + id));
        return mapToResponse(student);
    }

    @Override
    public StudentResponse updateStudent(Long id, StudentRequest request) {
        Student student = studentRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new NoSuchElementException("Student not found with id: " + id));

        student.setName(request.getName());
        student.setEmail(request.getEmail());
        student.setPhone(request.getPhone());
        student.setAddress(request.getAddress());

        Student updatedStudent = studentRepository.save(student);
        return mapToResponse(updatedStudent);
    }

    @Override
    public void deleteStudent(Long id) {
        Student student = studentRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new NoSuchElementException("Student not found with id: " + id));

        student.setDeleted(true);
        studentRepository.save(student);
    }

    @Override
    public Page<StudentResponse> searchStudentsByName(String name, Pageable pageable) {
        return studentRepository.findByNameContainingAndDeletedFalse(name, pageable)
                .map(this::mapToResponse);
    }

    // GPA recalculation using GradeCalculator
    public StudentResponse recalculateGPA(Long studentId) {
        Student student = studentRepository.findByIdAndNotDeleted(studentId)
                .orElseThrow(() -> new NoSuchElementException("Student not found with id: " + studentId));

        List<com.example.studentmanagement.entity.Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId)
                .stream()
                .filter(e -> e.getGrade() != null)
                .collect(Collectors.toList());

        // USE GradeCalculator Proper GPA calculation
        double gpa = gradeCalculator.calculateGPA(enrollments);
        student.setGpa(gpa);

        Student updatedStudent = studentRepository.save(student);

        System.out.println("Manually recalculated GPA for student " + studentId + ": " + gpa);

        return mapToResponse(updatedStudent);
    }

    public void softDeleteStudent(Long id) {
        Student student = studentRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new NoSuchElementException("Student not found with id: " + id));
        student.setDeleted(true);
        Student updatedStudent = studentRepository.save(student);
    }

    private StudentResponse mapToResponse(Student student) {
        StudentResponse response = new StudentResponse();
        response.setId(student.getId());
        response.setStudentId(student.getStudentId());
        response.setName(student.getName());
        response.setEmail(student.getEmail());
        response.setPhone(student.getPhone());
        response.setAddress(student.getAddress());
        response.setGpa(student.getGpa());
        return response;
    }
}