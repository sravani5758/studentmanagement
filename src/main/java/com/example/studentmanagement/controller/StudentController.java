package com.example.studentmanagement.controller;

import com.example.studentmanagement.dto.request.StudentRequest;
import com.example.studentmanagement.dto.response.StudentResponse;
import com.example.studentmanagement.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    public ResponseEntity<StudentResponse> createStudent(@RequestBody StudentRequest request) {
        return ResponseEntity.ok(studentService.createStudent(request));
    }

    @GetMapping
    public ResponseEntity<Page<StudentResponse>> getAllStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return ResponseEntity.ok(studentService.getAllStudents(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudentById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentResponse> updateStudent(
            @PathVariable Long id,
            @RequestBody StudentRequest request) {
        return ResponseEntity.ok(studentService.updateStudent(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/soft-delete")
    public void softDeleteStudent(@PathVariable Long id) {
        studentService.softDeleteStudent(id);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<StudentResponse>> searchStudents(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(studentService.searchStudentsByName(name, pageable));
    }

    // ✅ NEW ENDPOINT: Manual GPA recalculation using GradeCalculator
    @PostMapping("/{studentId}/recalculate-gpa")
    public ResponseEntity<StudentResponse> recalculateGPA(@PathVariable Long studentId) {
        return ResponseEntity.ok(studentService.recalculateGPA(studentId));
    }
}