package com.example.studentmanagement.controller;

import com.example.studentmanagement.dto.request.InstructorRequest;
import com.example.studentmanagement.dto.response.InstructorResponse;
import com.example.studentmanagement.dto.response.StudentResponse;
import com.example.studentmanagement.service.InstructorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/instructors")
@RequiredArgsConstructor
public class InstructorController {

    private final InstructorService instructorService;

    @PostMapping
    public ResponseEntity<InstructorResponse> createInstructor(@RequestBody @Valid InstructorRequest request) {
        return ResponseEntity.ok(instructorService.createInstructor(request));
    }

    @GetMapping
    public ResponseEntity<Page<InstructorResponse>> getAllInstructors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return ResponseEntity.ok(instructorService.getAllInstructors(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InstructorResponse> getInstructorById(@PathVariable Long id) {
        return ResponseEntity.ok(instructorService.getInstructorById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InstructorResponse> updateInstructor(
            @PathVariable Long id,
            @RequestBody InstructorRequest request) {
        return ResponseEntity.ok(instructorService.updateInstructor(id, request));
    }

    @DeleteMapping("/{id}/soft-delete")
    public ResponseEntity<Void> deleteInstructor(@PathVariable Long id) {
        instructorService.deleteInstructor(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/department/{department}")
    public ResponseEntity<List<InstructorResponse>> getInstructorsByDepartment(
            @PathVariable String department) {
        return ResponseEntity.ok(instructorService.getInstructorsByDepartment(department));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<InstructorResponse>> searchInstructor(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(instructorService.searchInstructorByName(name, pageable));
    }


    @GetMapping("/my-courses")
    public List<InstructorResponse> getMyCourses(Authentication authentication){
        return instructorService.getMyCourses();


    }


}